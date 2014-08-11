package org.wow.learning

import org.wow.learning.vectorizers.planet.PlanetTransition
import com.epam.starwors.galaxy.Planet
import org.wow.logger.World

data class InOutMove(val `in`: Int = 0, val out: Int = 0)

/**
 * Mahaut classifier accepts int numbers only
 * So lets convert out move to num
 * 100 - 0% in 0% out
 * 0 - 100% in 0% out
 * 200 - 0%in 100% out
 */
fun InOutMove.toInt() = when {
    this.`in` > 0 -> 100 - this.`in`
    else -> 100 + out
}

fun inOutMoveFromInt(num: Int) = when {
    num >=100 -> InOutMove(0, num - 100)
    else -> InOutMove(100 - num, 0)
}

/**
 *  Estimate, how many users were sent from/to planet this transition
 */
fun inOutPlanetCategorizer(state: PlanetTransition): InOutMove {
    val planetBeforeMove = state.from.planet
    val planetAfterMove = state.to.planet

    val expectedUnits = (planetBeforeMove.getUnits() + planetBeforeMove.getType()!!.getIncrement().toDouble() / 100 *
            planetBeforeMove.getUnits()).toInt()
    val unitsDifferencePercent =
            (Math.abs(planetBeforeMove.getUnits() - expectedUnits).toDouble() / planetBeforeMove.getUnits()).toInt()
    fun sumPlanetsPower(planets: List<Planet>, world: World) =
            planets.fold(0, {(acc, planet) -> planet.planetPower(world)})

    val enemyPowerDifference = sumPlanetsPower(planetAfterMove.enemiesNeighbours(), state.to.world) -
                                 sumPlanetsPower(planetBeforeMove.enemiesNeighbours(), state.from.world)
    val friendsPowerDifference = sumPlanetsPower(planetAfterMove.friendsNeighbours(), state.to.world) -
                                    sumPlanetsPower(planetBeforeMove.friendsNeighbours(), state.to.world)

    return when {
        planetBeforeMove.getOwner() != planetAfterMove.getOwner() -> InOutMove(100, 0)
        planetAfterMove.getUnits() == expectedUnits -> InOutMove(0,0)
        planetBeforeMove.enemiesAround() == 0 -> when {
            planetAfterMove.getUnits() < expectedUnits ->
                InOutMove(0, unitsDifferencePercent)
            else -> InOutMove(unitsDifferencePercent, 0)
        }
        planetBeforeMove.friendsAround() == 0 -> InOutMove(0, unitsDifferencePercent)
        friendsPowerDifference > 0 && enemyPowerDifference > 0 -> when {
            planetAfterMove.getUnits() < expectedUnits -> InOutMove(0, unitsDifferencePercent)
            else -> InOutMove(unitsDifferencePercent, 0)
        }

        else -> InOutMove(0,0)
    }
}
