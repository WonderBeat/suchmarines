package org.wow

import com.epam.starwors.game.SocketGame
import com.epam.starwors.bot.Logic
import org.wow.logger.GameLogger
import org.joda.time.DateTime
import java.io.FileOutputStream
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import org.wow.logger.LogsParser
import org.wow.evaluation.transition.BestTransitionsFinder
import org.wow.evaluation.UserPowerEvaluator
import org.wow.learning.MachineLearner
import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression
import org.apache.mahout.classifier.sgd.L1
import org.wow.learning.vectorizers.planet.PlanetVectorizer
import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.predict.InOutPlanetPredictor
import org.apache.mahout.math.Vector

import java.io.File
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder
import org.wow.learning.vectorizers.planet.FeatureExtractor
import org.wow.learning.enemiesAroundPercentage
import org.wow.learning.friendsAroundPercentage
import org.wow.learning.planetPower
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder
import org.wow.logic.PredictionAwareBot
import org.wow.learning.vectorizers.Vectorizer
import org.wow.learning.categorizers.inOutCategorizer
import org.wow.learning.neutralNeighbours
import org.wow.logger.World
import org.wow.learning.persist.FileClassifierProvider
import org.apache.mahout.classifier.sgd.CrossFoldLearner
import org.wow.learning.persist.FileDbBuilder
import org.wow.learning.vectorizers.planet.transitionToPlanetTransition


fun main(args : Array<String>) {
    val username = "suchbotwow"
    val categoriesCount = 201 // 0 - 200 inclusive
    val objectMapper = ObjectMapper(SmileFactory())
    val planetFeatoresExtractors = listOf(
            FeatureExtractor(ContinuousValueEncoder("enemies-around"), {state -> state.planet.enemiesAroundPercentage().toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("neutrals-around"), {state -> state.planet.neutralNeighbours().size.toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("friends-around"), {s -> s.planet.friendsAroundPercentage().toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("planet-power"), {s -> s.planet.planetPower(s.world).toDouble()}),
            FeatureExtractor(ConstantValueEncoder("planet-size"), {s -> s.planet.getType()!!.getLimit().toDouble()}),
            FeatureExtractor(ConstantValueEncoder("planet-connections"), {s -> s.planet.getNeighbours()!!.size.toDouble()})
    )
    val planetVectorizer = PlanetVectorizer(planetFeatoresExtractors)
    val firstStateInTransitionVectorizer = object: Vectorizer<PlanetTransition, Vector> {
        override fun vectorize(input: PlanetTransition) = planetVectorizer.vectorize(input.from)
    }

    val bestFinder = BestTransitionsFinder(UserPowerEvaluator())
    val bestMovesInGameFinder = { (game: List<World>) ->
        bestFinder.findBestTransitions(game).flatMap(::transitionToPlanetTransition) }

    val learner = MachineLearner(::inOutCategorizer, firstStateInTransitionVectorizer,
            AdaptiveLogisticRegression(categoriesCount, planetFeatoresExtractors.size, L1()))
    val extractClassifierFromRegression: (AdaptiveLogisticRegression) -> CrossFoldLearner = { it.close(); it.getBest()!!
            .getPayload()!!.getLearner()!! }

    FileDbBuilder("games.db", LogsParser(objectMapper, "dump/"), bestMovesInGameFinder, learner).create()
    val classifierProvider = FileClassifierProvider("games.db", { AdaptiveLogisticRegression(categoriesCount,
                    planetFeatoresExtractors.size, L1()) }, extractClassifierFromRegression)
    val trainedClassifier = classifierProvider.provide()

    var predictor = InOutPlanetPredictor(
            {(v: Vector) -> trainedClassifier.classifyFull(v)!! },
            planetVectorizer)

    val gameLogger = GameLogger(objectMapper.writer()!!)


    val game = SocketGame("176.192.95.4", 10040, "bzk6w4awpfdhbdnnqv4ziaocvjkumbtn",
            filterEmptyWorlds(gameLogger).and(PredictionAwareBot(username, predictor)))
    print("Running....")
    game.start()

    val file = File("dump/" + DateTime.now()!!.toString("MMddhhmmss") + ".dmp")
    file.getParentFile()!!.mkdirs();
    file.createNewFile();
    FileOutputStream(file).write(gameLogger.dump());
}

fun filterEmptyWorlds(delegate: Logic) = Logic { planets -> if(planets!!.empty) arrayListOf() else delegate.step(planets) }

fun Logic.and(other: Logic): Logic =
        Logic { planets-> this.step(planets)?.plus(other.step(planets)!!)?.toArrayList() }
