package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.enemiesAroundPercentage
import org.wow.learning.friendsAroundPercentage
import org.wow.learning.enemiesNeighbours
import org.wow.learning.unitsAfterRegeneration
import org.wow.learning.friendsNeighbours
import org.wow.learning.neutralNeighbours
import org.wow.learning.planetPower
import org.wow.learning.isolationLevel
import com.epam.starwors.galaxy.Planet

trait Move

object UndefinedMove: Move

data class InOutMove(val `in`: Int = 0, val out: Int = 0): Move

/**
 * Mahaut classifier accepts int numbers only
 * So lets convert out move to num
 * 100 - 0% in 0% out
 * 0 - 100% in 0% out
 * 200 - 0%in 100% out
 */
fun InOutMove.toInt(): Int = when {
    this.`in` > 100 -> 0
    this.out > 100 -> 200
    this.`in` > 0 -> 100 - this.`in`
    else -> 100 + out
}

fun inOutMoveFromInt(num: Int): InOutMove = when {
    num >= 100 -> InOutMove(0, num - 100)
    else -> InOutMove(100 - num, 0)
}


fun PlanetTransition.unitsDifferencePercentage(): Int =
        (Math.abs(this.to.planet.getUnits() - this.from.planet.unitsAfterRegeneration()).toDouble() * 100 / this.from.planet.getUnits().toDouble()).toInt()

fun isolatedPlanet(transition: PlanetTransition): Move =  when {
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
                                            -> isolatedPlanet(transition)
    else -> UndefinedMove
}

fun frontLiner(transition: PlanetTransition): Move = when {
    transition.from.planet.neutralNeighbours().size > 0 -> UndefinedMove
    transition.from.planet.isolationLevel() > 1.0 -> InOutMove(`in` = transition.unitsDifferencePercentage())
    else -> UndefinedMove
}

fun planetSurroundedByNoEnemies(transition: PlanetTransition): Move = when {
    transition.from.planet.enemiesAroundPercentage().toInt() == 0 -> isolatedPlanet(transition)
    else -> UndefinedMove
}

/**
 * Attack only
 */
fun planetSurroundedByNoFriends(transition: PlanetTransition): Move = when {
    transition.from.planet.friendsAroundPercentage().toInt() == 0 -> InOutMove(0, transition.unitsDifferencePercentage())
    else -> UndefinedMove
}

/**
 * Worst estimator ever. But we need to categorize this situation
 */
fun noPlanetsWereCaptured(transition: PlanetTransition): Move = when {
    transition.from.planet.enemiesNeighbours().size == transition.to.planet.enemiesNeighbours().size -> isolatedPlanet(transition)
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
    fun enemyAndNeighbors(planet: Planet): List<Planet> = planet.enemiesNeighbours().plus(planet.neutralNeighbours())
    val othersAfter = enemyAndNeighbors(transition.to.planet).map { it.getId() }.toSet()
    val captured = enemyAndNeighbors(transition.to.planet).filter { !othersAfter.contains(it.getId()) }
    return when {
        captured.size > 0 && captured.any { p -> p.getOwner() == transition.from.planet.getOwner() } ->
            InOutMove(out = transition.unitsDifferencePercentage())
        else -> UndefinedMove
    }
}

