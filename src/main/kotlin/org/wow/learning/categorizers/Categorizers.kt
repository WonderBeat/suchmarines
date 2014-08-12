package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.enemiesAround
import org.wow.learning.friendsAround
import org.wow.learning.enemiesNeighbours
import org.wow.learning.unitsAfterRegeneration
import org.wow.learning.friendsNeighbours
import org.wow.learning.neutralNeighbours
import org.wow.learning.planetPower

trait Move

object UndefinedMove: Move

data class InOutMove(val `in`: Int = 0, val out: Int = 0): Move

fun PlanetTransition.unitsDifferencePercentage(): Int =
        (Math.abs(this.to.planet.getUnits() - this.from.planet.unitsAfterRegeneration()).toDouble() * 100 / this.from.planet.getUnits().toDouble()).toInt()

fun estimateMoveWithUnitsDifference(transition: PlanetTransition): Move =  when {
    transition.to.planet.getUnits() < transition.from.planet.unitsAfterRegeneration() -> InOutMove(out = transition.unitsDifferencePercentage())
    else -> InOutMove(`in` = transition.unitsDifferencePercentage())
}

fun planetWasCaptured(transition: PlanetTransition): Move =
    if (transition.from.planet.getOwner() != transition.to.planet.getOwner()) InOutMove(100, 0) else UndefinedMove

fun planetMadeNoMove(transition: PlanetTransition): Move = when {
    transition.to.planet.getUnits() == transition.from.planet.unitsAfterRegeneration() -> InOutMove()
    else -> UndefinedMove
}

fun planetSurroundedByNeutrals(transition: PlanetTransition): Move = when {
    transition.from.planet.friendsNeighbours().size == 0 && transition.from.planet.enemiesNeighbours().size == 0
                                            -> InOutMove(out = transition.unitsDifferencePercentage())
    else -> UndefinedMove
}

fun planetSurroundedByNoEnemies(transition: PlanetTransition): Move = when {
    transition.from.planet.enemiesAround() == 0 -> estimateMoveWithUnitsDifference(transition)
    else -> UndefinedMove
}

/**
 * Attack only
 */
fun planetSurroundedByNoFriends(transition: PlanetTransition): Move = when {
    transition.from.planet.friendsAround() == 0 -> InOutMove(0, transition.unitsDifferencePercentage())
    else -> UndefinedMove
}

/**
 * Worst estimator ever. But we need to categorize this situation
 */
fun noPlanetsWereCaptured(transition: PlanetTransition): Move = when {
    transition.from.planet.enemiesNeighbours().size == transition.to.planet.enemiesNeighbours().size -> estimateMoveWithUnitsDifference(transition)
    else -> UndefinedMove
}

/**
 * If all enemies are stronger and no neutrals around and units number increased more than regen ->
 * it was a defend move!
 */
fun allEnemiesStronger(transition: PlanetTransition): Move = when {
    transition.from.planet.neutralNeighbours().size > 0 -> UndefinedMove
    transition.from.planet.unitsAfterRegeneration() < transition.to.planet.getUnits() &&
            transition.from.planet.enemiesNeighbours().all { it.planetPower(transition.from.world) >
            transition.from.planet.planetPower(transition.from.world) } -> InOutMove(`in` = transition.unitsDifferencePercentage())
    else -> UndefinedMove
}

fun planetWasCapturedByCurrentPlanet(transition: PlanetTransition): Move {
    val enemiesAfterMove = transition.to.planet.enemiesNeighbours().map { it.getId() }.toSet()
    val capturedEnemies = transition.from.planet.enemiesNeighbours().filter { !enemiesAfterMove.contains(it.getId()) }
    return when {
        capturedEnemies.size == 1 && capturedEnemies.first!!.enemiesNeighbours().size == 1 -> InOutMove(out = transition.unitsDifferencePercentage())
        else -> UndefinedMove
    }
}

