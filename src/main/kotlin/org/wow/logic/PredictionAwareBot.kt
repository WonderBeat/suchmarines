package org.wow.logic

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import org.wow.learning.predict.Predictor
import org.wow.learning.predict.PlanetMovePrediction
import org.wow.logger.World
import org.wow.learning.enemiesNeighbours
import org.wow.learning.planetPower
import org.slf4j.LoggerFactory

data class PlanetMovePredict(val planet: Planet, val predict: PlanetMovePrediction)

private fun Planet.unitsFromPercent(percent: Int): Int = ((this.getUnits() * percent).toDouble() / 100.toDouble()).toInt()

public class PredictionAwareBot(val username: String,
        val inOutPredictor: Predictor<PlanetMovePrediction, Planet>): Logic {

    private val logger = LoggerFactory.getLogger(javaClass<PredictionAwareBot>())!!

    override fun step(planets: Collection<Planet>?): MutableCollection<Move>? {
        val predictsForPlanets = predictPlanets(planets!!)
        logger.info(predictsForPlanets.toString())

        val requiresDefend = predictsForPlanets.filter { it.predict.`in` > 0 }
        val readyForUnitsTransfer = predictsForPlanets.filter { it.predict.out > 0 }

        return readyForUnitsTransfer.flatMap { readyToSendPlanet ->
            val couldSend = readyToSendPlanet.planet.unitsFromPercent(readyToSendPlanet.predict.out)
            val leastPowerNeighbour =
                    readyToSendPlanet.planet.getNeighbours()!!.minBy { it.planetPower(World(planets)) }
            val requiresDefendNeighbours = requiresDefend.filter { readyToSendPlanet.planet.getNeighbours()!!.contains(it) }

            fun createDefendMove(unitsLeft: Int, planetToDefend: PlanetMovePredict): Move? {
                val requires = planetToDefend.planet.unitsFromPercent(planetToDefend.predict.`in`)
                return when {
                    unitsLeft == 0 -> null
                    else -> Move(readyToSendPlanet.planet, planetToDefend.planet, Integer.min(unitsLeft, requires))
                }
            }

            val defendMoves: List<Move> = requiresDefendNeighbours.fold(arrayListOf(), { (moves : List<Move>, planetToDefend) ->
                val move = createDefendMove(couldSend - countUnits(moves), planetToDefend)
                if (move != null) { moves + move } else { moves }
            })
            val leftForAttackUnits = couldSend - countUnits(defendMoves)
            when {
                leftForAttackUnits > 0 -> defendMoves.plus(Move(readyToSendPlanet.planet, leastPowerNeighbour, leftForAttackUnits))
                else -> defendMoves
            }
        }.toArrayList()
    }

    fun countUnits(moves: List<Move>): Int = moves.fold(0, {sum, move -> sum + move.getAmount() })

    fun predictPlanets(planets: Collection<Planet>): List<PlanetMovePredict> {
        return planets.filter { it.getOwner() == username }.map {
            PlanetMovePredict(it, inOutPredictor.predict(it, World(planets)))
        }
    }


}
