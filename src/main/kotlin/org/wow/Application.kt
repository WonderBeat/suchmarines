package org.wow

import com.epam.starwors.game.SocketGame
import com.epam.starwors.bot.Logic
import org.wow.logger.GameLogger
import org.joda.time.DateTime
import java.io.FileOutputStream
import com.fasterxml.jackson.databind.ObjectMapper
import org.wow.evaluation.transition.BestTransitionsFinder
import org.wow.evaluation.UserPowerEvaluator
import org.wow.learning.vectorizers.planet.PlanetVectorizer
import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.predict.InOutPlanetPredictor
import org.apache.mahout.math.Vector

import java.io.File
import org.wow.learning.vectorizers.planet.FeatureExtractor
import org.wow.logic.PredictionAwareBot
import org.wow.learning.vectorizers.Vectorizer
import org.wow.logger.GameTurn
import org.wow.learning.persist.FileClassifierProvider
import reactor.core.Environment
import org.wow.logger.LogsParser
import org.wow.learning.FileBasedLearner
import org.wow.learning.MachineLearner
import org.wow.learning.categorizers.inOutCategorizer
import org.springframework.core.io.FileSystemResource
import java.io.DataOutputStream
import org.apache.http.impl.client.HttpClients
import org.wow.http.HttpGameClient
import com.fasterxml.jackson.databind.DeserializationFeature
import org.wow.learning.volumePercentage
import org.wow.learning.enemiesAroundPercentage
import org.wow.learning.friendsAroundPercentage
import org.wow.learning.neutralAroundPercentage
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder
import org.wow.learning.vectorizers.planet.planetMoves
import com.epam.starwors.galaxy.Planet
import org.apache.mahout.classifier.sgd.L1
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression
import java.util.Random
import com.epam.starwors.galaxy.Move
import org.wow.logic.UniformAttack
import org.wow.logic.TrainOne

public class Application() {

    private val dbFile = "games.db"
    private val username = "suchbotwownotacat"
    private val dumpFolder = "dump/"
    private val gameUrl = "176.192.95.4"
    private val port = 10040
    private val key = "bzk6w4awpfdhbdnnqv4ziaocvjkumbtn"

    val env = Environment()

    val httpClient = HttpClients.createDefault()!!

    val jsonMapper = {
        val jsonMapper = ObjectMapper()
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        jsonMapper
    }()

    val planetFeaturesExtractors = listOf(
            FeatureExtractor(ContinuousValueEncoder("volume"), {s -> s.volumePercentage()}, 1.0),
            FeatureExtractor(ContinuousValueEncoder("enemies-around"), {s -> s.enemiesAroundPercentage()}, 1.0),
            FeatureExtractor(ContinuousValueEncoder("friends-around"), {s -> s.friendsAroundPercentage()}, 1.0),
            FeatureExtractor(ContinuousValueEncoder("neutral-around"), {s -> s.neutralAroundPercentage()}, 1.0),
            FeatureExtractor(ContinuousValueEncoder("planet-size"), {s -> s.getType()!!.ordinal().toDouble()}, 25.0),
            FeatureExtractor(ContinuousValueEncoder("neighbors"), {s -> s.getNeighbours()!!.size.toDouble()}, 9.0)
    )

    val bestMovesInGameFinder = {
        val bestTransitionsFinder = BestTransitionsFinder(UserPowerEvaluator())
        val bestMoves = { (game: List<GameTurn>) ->
            bestTransitionsFinder.findBestTransitions(game).flatMap(::planetMoves)
        }
        bestMoves
    }()

    val planetVectorizer : Vectorizer<Planet, Vector> = PlanetVectorizer(planetFeaturesExtractors)

    val firstStateInTransitionVectorizer = object: Vectorizer<PlanetTransition, Vector> {
        override fun vectorize(input: PlanetTransition) =
                planetVectorizer.vectorize(input.from)
    }

    val machineLearner = createMachineLearner()

    val classifier = machineLearner.learner

    val httpGameClient = HttpGameClient(httpClient, jsonMapper, "http://" + gameUrl)

    fun start(login: String, password: String) {
        httpGameClient.login(login, password)
        val machine = createMachineBot()
        val gameId = httpGameClient.startGame()
        val gameLogger = GameLogger(jsonMapper, gameId , httpGameClient)
        val game = SocketGame(gameUrl, port, key,
                machine.and(gameLogger))
        print("Running....")
        game.start()
        httpGameClient.endGame(gameId)
        saveGameLogs(gameLogger)
    }

    fun saveGameLogs(gameLogger: GameLogger) {
        val file = File(dumpFolder + DateTime.now()!!.toString("MMddhhmmss") + ".dmp")
        file.getParentFile()!!.mkdirs();
        file.createNewFile();
        FileOutputStream(file).write(gameLogger.dump());

        bestMovesInGameFinder(gameLogger.states).forEach { machineLearner.learn(it) } // learn last game
        saveRegressionToFile()
    }

    fun createMachineBot(): Logic {
        var predictor = InOutPlanetPredictor(
                {(v: Vector) -> classifier.classifyFull(v)!! },
                planetVectorizer)
        return PredictionAwareBot(username, predictor)
    }

    fun createMachineLearner(): MachineLearner<PlanetTransition, OnlineLogisticRegression> {
        val categoriesCount = 201 // 0 - 200 inclusive
        val objectMapper = ObjectMapper()
        val regression = OnlineLogisticRegression(categoriesCount, planetFeaturesExtractors.size, L1())
        return when {
            !FileSystemResource(dbFile).exists() && !allDumpsInFolder(dumpFolder).empty -> {
                val classifierFromDump = createClassifierFromDump(regression, objectMapper)
                classifierFromDump
            }
            FileSystemResource(dbFile).exists() ->  {
                val trainedRegression = FileClassifierProvider<OnlineLogisticRegression>(dbFile).provide(regression)
                MachineLearner(::inOutCategorizer, firstStateInTransitionVectorizer, trainedRegression)
            }
            else -> MachineLearner(::inOutCategorizer, firstStateInTransitionVectorizer, regression)
        }
    }

    fun createClassifierFromDump(regression: OnlineLogisticRegression, objectMapper: ObjectMapper):
            MachineLearner<PlanetTransition,
                    OnlineLogisticRegression> {
        val fileBasedLearner = FileBasedLearner(env, allDumpsInFolder(dumpFolder),
                LogsParser(objectMapper), bestMovesInGameFinder)
        val learner = MachineLearner(::inOutCategorizer, firstStateInTransitionVectorizer, regression)
        val machineLearnerPrepared = fileBasedLearner.learn(learner)
        return machineLearnerPrepared
    }

    fun saveRegressionToFile() {
        val file = File(dbFile)
        file.createNewFile()
        machineLearner.learner.close()
        val dataOutputStream = DataOutputStream(FileSystemResource(file).getOutputStream()!!)
        machineLearner.learner.write(dataOutputStream)
        dataOutputStream.flush()
    }

}

fun main(args: Array<String>) {
    Application().start(args.get(0), args.get(1))
}

fun filterEmptyWorlds(delegate: Logic) = Logic { planets -> if(planets!!.empty) arrayListOf() else delegate.step(planets) }

fun allDumpsInFolder(folder: String):List<File> =
    File(folder).listFiles { it.extension.equals("dmp") }?.toArrayList() ?: arrayListOf()

fun Logic.and(other: Logic): Logic =
        Logic { planets-> this.step(planets)?.plus(other.step(planets)!!)?.toArrayList() }
