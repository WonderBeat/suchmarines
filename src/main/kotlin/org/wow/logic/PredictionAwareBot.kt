package org.wow.logic

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import org.wow.learning.predict.Predictor
import org.wow.logger.World
import org.wow.learning.planetPower
import org.slf4j.LoggerFactory
import org.wow.learning.enemiesNeighbours
import org.wow.learning.neutralNeighbours
import org.wow.learning.friendsNeighbours
import org.wow.learning.percentUsers
import org.wow.learning.categorizers.InOutMove

data class PlanetMovePredict(val planet: Planet, val predict: InOutMove)

public class PredictionAwareBot(val username: String,
        val inOutPredictor: Predictor<InOutMove, Planet>): Logic {

    private val logger = LoggerFactory.getLogger(javaClass<PredictionAwareBot>())!!

    override fun step(planets: Collection<Planet>?): MutableCollection<Move>? {
        val predictsForPlanets = predictPlanets(planets!!)
        logger.info(predictsForPlanets.toString())

        val requiresDefend = predictsForPlanets.filter { it.predict.`in` > 0 }
        val readyForUnitsTransfer = predictsForPlanets.filter { it.predict.out > 0 }

        val moves = readyForUnitsTransfer.flatMap { readyToSendPlanet ->
            val couldSend = readyToSendPlanet.planet.percentUsers(readyToSendPlanet.predict.out)
            val leastPowerEnemy = readyToSendPlanet.planet.enemiesNeighbours().minBy { it.planetPower(World(planets)) }
            val leastPowerFriend = readyToSendPlanet.planet.friendsNeighbours().minBy { it.planetPower(World(planets)) }
            val neutralPlanetNeighbor = readyToSendPlanet.planet.neutralNeighbours().first
            val requiresDefendNeighbours = requiresDefend.filter { readyToSendPlanet.planet.getNeighbours()!!.contains(it) }

            fun createDefendMove(unitsLeft: Int, planetToDefend: PlanetMovePredict): Move? {
                val requires = planetToDefend.planet.percentUsers(planetToDefend.predict.`in`)
                return when {
                    unitsLeft == 0 -> null
                    else -> Move(readyToSendPlanet.planet, planetToDefend.planet, Integer.min(unitsLeft, requires))
                }
            }

            val defendMoves: List<Move> = requiresDefendNeighbours.fold(arrayListOf(), {(moves: List<Move>, planetToDefend) ->
                val move = createDefendMove(couldSend - countUnits(moves), planetToDefend)
                if (move != null) {
                    moves + move
                } else {
                    moves
                }
            })
            val leftForAttackUnits = couldSend - countUnits(defendMoves)
            val maxPossible = readyToSendPlanet.planet.getUnits()
            if(maxPossible < leftForAttackUnits) {
                var a = 5
                ++a
            }
            when {
                leftForAttackUnits > 0 -> when {
                    leastPowerEnemy != null -> defendMoves.plus(Move(readyToSendPlanet.planet, leastPowerEnemy,
                            leftForAttackUnits))
                    neutralPlanetNeighbor != null -> defendMoves.plus(Move(readyToSendPlanet.planet, neutralPlanetNeighbor,
                            leftForAttackUnits))
                    leastPowerFriend != null -> defendMoves.plus(Move(readyToSendPlanet.planet, leastPowerFriend,
                            leftForAttackUnits))
                    else -> defendMoves
                }
                else -> defendMoves
            }
        }.toArrayList()
        return moves
    }

    fun countUnits(moves: List<Move>): Int = moves.fold(0, {sum, move -> sum + move.getAmount() })

    fun predictPlanets(planets: Collection<Planet>): List<PlanetMovePredict> {
        return planets.filter { it.getOwner() == username }.map {
            PlanetMovePredict(it, inOutPredictor.predict(it, World(planets)))
        }
    }


}
