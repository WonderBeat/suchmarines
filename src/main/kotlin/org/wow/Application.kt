package org.wow

import com.epam.starwors.game.SocketGame
import com.epam.starwors.bot.Logic
import org.wow.logger.GameLogger
import org.joda.time.DateTime
import java.io.FileOutputStream
import com.fasterxml.jackson.databind.ObjectMapper
import org.wow.evaluation.transition.BestTransitionsFinder
import org.wow.evaluation.UserPowerEvaluator
import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression
import org.apache.mahout.classifier.sgd.L1
import org.wow.learning.vectorizers.planet.PlanetVectorizer
import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.predict.InOutPlanetPredictor
import org.apache.mahout.math.Vector

import java.io.File
import org.wow.learning.vectorizers.planet.FeatureExtractor
import org.wow.learning.enemiesAroundPercentage
import org.wow.learning.friendsAroundPercentage
import org.wow.logic.PredictionAwareBot
import org.wow.learning.vectorizers.Vectorizer
import org.wow.learning.neutralNeighbours
import org.wow.logger.GameTurn
import org.wow.learning.persist.FileClassifierProvider
import org.apache.mahout.classifier.sgd.CrossFoldLearner
import org.wow.learning.vectorizers.planet.transitionToPlanetTransition
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder
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
import com.epam.starwors.galaxy.Planet

private val dbFile = "games.db"
private val username = "suchbotwow"
private val dumpFolder = "dump/"
private val gameUrl = "176.192.95.4"
private val port = 10040
private val key = "bzk6w4awpfdhbdnnqv4ziaocvjkumbtn"

fun httpClient(): org.apache.http.client.HttpClient = HttpClients.createDefault()!!

fun jsonMapper(): ObjectMapper {
    val jsonMapper = ObjectMapper()
    jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return jsonMapper
}

fun main(args: Array<String>) {
    val jsonMapper = jsonMapper()
    val httpClient = HttpGameClient(httpClient(), jsonMapper, "http://" + gameUrl)
    httpClient.login(args.get(0), args.get(1))
    val machine = createMachineBot()
    val gameId = httpClient.startGame()
    val gameLogger = GameLogger(jsonMapper, gameId , httpClient)
    val game = SocketGame(gameUrl, port, key, machine.and(gameLogger))
    print("Running....")
    game.start()
    httpClient.endGame(gameId)

    val file = File(dumpFolder + DateTime.now()!!.toString("MMddhhmmss") + ".dmp")
    file.getParentFile()!!.mkdirs();
    file.createNewFile();
    FileOutputStream(file).write(gameLogger.dump());
}

fun createMachineBot(): Logic {
    val categoriesCount = 201 // 0 - 200 inclusive
    val objectMapper = ObjectMapper()
    val env = Environment()


    val planetFeaturesExtractors = arrayListOf(
            FeatureExtractor(ContinuousValueEncoder("enemies-around"), {it.enemiesAroundPercentage().toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("neutrals-around"), { it.neutralNeighbours().size.toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("friends-around"), { it.friendsAroundPercentage().toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("planet-size"), {it.getType()!!.ordinal().toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("planet-connections"), {it.getNeighbours()!!.size.toDouble()})
    )
    val regressionBuilder = { AdaptiveLogisticRegression(categoriesCount, planetFeaturesExtractors.size, L1()) }
    val extractClassifierFromRegression: (AdaptiveLogisticRegression) -> CrossFoldLearner = { it.close(); it.getBest()!!
            .getPayload()!!.getLearner()!! }
    val planetVectorizer = PlanetVectorizer(planetFeaturesExtractors)

    if(!FileSystemResource(dbFile).exists() && !allFilesInFolder(dumpFolder).empty) {
        createDb(regressionBuilder(), planetVectorizer, env, objectMapper)
    }

    val classifierProvider = FileClassifierProvider(dbFile, regressionBuilder, extractClassifierFromRegression)
    val trainedClassifier = classifierProvider.provide()

    var predictor = InOutPlanetPredictor(
            {(v: Vector) -> trainedClassifier.classifyFull(v)!! },
            planetVectorizer)
    return PredictionAwareBot(username, predictor)
}

fun createDb(regression: AdaptiveLogisticRegression,
             planetVectorizer: Vectorizer<Planet, Vector>,
             env: Environment,
             objectMapper: ObjectMapper) {
    val bestFinder = BestTransitionsFinder(UserPowerEvaluator())
    val bestMovesInGameFinder = { (game: List<GameTurn>) ->
        bestFinder.findBestTransitions(game).flatMap(::transitionToPlanetTransition) }
    val firstStateInTransitionVectorizer = object: Vectorizer<PlanetTransition, Vector> {
        override fun vectorize(input: PlanetTransition) = planetVectorizer.vectorize(input.from)
    }

    val learner = MachineLearner(::inOutCategorizer, firstStateInTransitionVectorizer, regression)
    val fileBasedLearner = FileBasedLearner(env, allFilesInFolder(dumpFolder),
            LogsParser(objectMapper), bestMovesInGameFinder)
    val machineLearnerPrepared = fileBasedLearner.learn(learner)
    machineLearnerPrepared.learner.close()
    val file = File(dbFile)
    file.createNewFile()
    machineLearnerPrepared.learner.write(DataOutputStream(FileSystemResource(file).getOutputStream()!!))
}

fun filterEmptyWorlds(delegate: Logic) = Logic { planets -> if(planets!!.empty) arrayListOf() else delegate.step(planets) }

fun allFilesInFolder(folder: String):List<File> {
    val list = File(folder).listFiles { it.extension.equals("dmp") }?.toArrayList()
    return when {
        list == null -> arrayListOf()
        else -> list
    }
}

fun Logic.and(other: Logic): Logic =
        Logic { planets-> this.step(planets)?.plus(other.step(planets)!!)?.toArrayList() }
