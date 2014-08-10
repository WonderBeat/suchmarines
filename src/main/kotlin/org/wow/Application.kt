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
import org.wow.learning.Categorizator
import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression
import org.apache.mahout.classifier.sgd.L1
import org.wow.learning.vectorizers.planet.PlanetVectorizer
import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.predict.InOutPlanetPredictor
import org.apache.mahout.math.Vector
import org.wow.logger.World
import com.epam.starwors.galaxy.Move

import java.io.File
import org.wow.learning.vectorizers.planet.PlanetState
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder
import org.wow.learning.vectorizers.planet.FeatureExtractor
import org.wow.learning.enemiesAround
import org.wow.learning.friendsAround
import org.wow.learning.planetPower
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder
import org.apache.mahout.classifier.OnlineLearner

fun main(args : Array<String>) {
    val username = "WooDmaN"
    val objectMapper = ObjectMapper(SmileFactory())
    val planetFeatoresExtractors = listOf(
            FeatureExtractor(ContinuousValueEncoder("enemies-around"), {state -> state.planet.enemiesAround(state
                    .world).toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("friends-around"), {s -> s.planet.friendsAround(s.world).toDouble()}),
            FeatureExtractor(ContinuousValueEncoder("planet-power"), {s -> s.planet.planetPower(s.world).toDouble()}),
            FeatureExtractor(ConstantValueEncoder("planet-size"), {s -> s.planet.getType()!!.getLimit().toDouble()})
    )
    val planetVectorizer = PlanetVectorizer(planetFeatoresExtractors)

    val bestFinder = BestTransitionsFinder(UserPowerEvaluator())
    val bestPlanetTransitions = LogsParser(objectMapper).parse("dump/").flatMap { game ->
        val bestMoves = bestFinder.findBestTransitions(game)
        bestMoves.flatMap { transition ->
            transition.sourceWorld.planets!!.filter { it.getOwner() == transition.playerName }.map {
                PlanetTransition(PlanetState(transition.sourceWorld, it), PlanetState(transition.resultWorld, it))
            }
        }
    }

    val categorizator = Categorizator({ 142 }, planetVectorizer ,
            AdaptiveLogisticRegression(200, planetFeatoresExtractors.size, L1()))
    val trainedMachine =  categorizator.learn(bestPlanetTransitions.map { it.from })
    trainedMachine.close()

    var predictor = InOutPlanetPredictor(
            {(v: Vector) -> trainedMachine.getBest()!!.getPayload()!!.getLearner()!!.classifyFull(v)!! },
            planetVectorizer)

    var logic = Logic { planets ->
        val predicts = planets!!.filter { it.getOwner() == username }.map { Pair(it, predictor.predict(it, World(planets))) }
        predicts.map { planetPredict ->
            val planet = planetPredict.first
            val predict = planetPredict.second
            when {
                    predict.`in` > 0 -> Move(planet, planet, 0)
                else -> {
                    var firstEnemy = planet.getNeighbours()!!.filter { it.getOwner() != username }.first
                    if(firstEnemy != null) {
                        Move(planet, firstEnemy, predict.out)
                    } else {
                        null
                    }

                }
        }
      }.filterNotNull().toArrayList()
    }

    val gameLogger = GameLogger(objectMapper.writer()!!)


    val game = SocketGame("176.192.95.4", 10040, "wpm5dqloq5s6kzxem4j5ixaw4tlu6dee", gameLogger.and(logic))
    print("Running....")
    game.start()

    val file = File("dump/" + DateTime.now()!!.toString("MMddhhmmss") + ".dmp")
    file.getParentFile()!!.mkdirs();
    file.createNewFile();
    FileOutputStream(file).write(gameLogger.dump());
    //FileSystemResource("dump/" + DateTime.now()!!.toString("MMddhhmmss") + ".dmp").getOutputStream()!!.write(gameLogger.dump())
}

fun Logic.and(other: Logic): Logic =
        Logic { planets-> this.step(planets)?.plus(other.step(planets)!!)?.toArrayList() }
