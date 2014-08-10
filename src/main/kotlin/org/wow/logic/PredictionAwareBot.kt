package org.wow.logic

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import org.wow.learning.predict.Predictor
import org.wow.learning.predict.PlanetMovePrediction
import org.wow.logger.World

public class PredictionAwareBot(val username: String,
        val inOutPredictor: Predictor<PlanetMovePrediction, Planet>): Logic {

    override fun step(planets: Collection<Planet>?): MutableCollection<Move>? {
        val predictsForPlanets = predictPlanets(planets!!)
        return predictsForPlanets.map { planetPredict ->
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

    fun predictPlanets(planets: Collection<Planet>): List<Pair<Planet, PlanetMovePrediction>> {
        return planets.filter { it.getOwner() == username }.map {
            Pair(it,
                    inOutPredictor.predict(it, World(planets)))
        }
    }


}
