package org.wow.learning

import org.wow.logger.GameTurn
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType

fun Planet.enemiesNeighbours(): List<Planet> = this.getNeighbours()!!.filter { it.getOwner() != this.getOwner() && it
        .getOwner().isNotEmpty() }

fun Planet.friendsNeighbours(): List<Planet> = this.getNeighbours()!!.filter { it.getOwner() == this.getOwner() }

fun Planet.neutralNeighbours(): List<Planet> = this.getNeighbours()!!.filter { it.getOwner()!!.isEmpty() }

fun Collection<Planet>.sumUnits(): Int = this.fold(0) { acc, it -> acc + it.getUnits() }

/**
 * Enemies around percentage
 */
fun Planet.friendsAroundPercentage(): Int {
    val allUnits = this.getNeighbours()!!.sumUnits()
    val friendUnits = this.friendsNeighbours().sumUnits()
    return when {
        allUnits == 0 -> 0
        else -> (100 * friendUnits) / allUnits
    }
}

/**
 * Enemies around percentage
 */
fun Planet.enemiesAroundPercentage(): Int {
    val allUnits = this.getNeighbours()!!.sumUnits()
    val enemyUnits = this.enemiesNeighbours().sumUnits()
    return when {
        allUnits == 0 -> 0
        else -> (100 * enemyUnits) / allUnits
    }
}

/**
 * Planet power
 *
 * All planets power = 100%
 * Current planet = ?
 */
fun Planet.planetPower(world: Collection<Planet>): Double
    = 100 * absolutePlanetPower(this) / world.fold(0.0) { acc, it -> acc + absolutePlanetPower(it) }

private fun absolutePlanetPower(planet: Planet): Double
        = planet.getUnits() * 100.0 / planet.getType()!!.getLimit() +
          planet.getType()!!.getLimit() * 100.0 / PlanetType.TYPE_D.getLimit()


/**
 * Isolation level %
 * enemy num / friends num
 */
fun Planet.isolationLevel(): Double = this.enemiesAroundPercentage().toDouble() / this.friendsAroundPercentage()

fun Planet.unitsAfterRegeneration(): Int {
    val expected = (this.getUnits() + this.getType()!!.getIncrement() * 0.01 *
            this.getUnits()).toInt()
    return when {
        expected > this.getType()!!.getLimit() -> this.getType()!!.getLimit()
        else -> expected
    }
}

fun Planet.percentUsers(percent: Int): Int = ((this.getUnits() * percent).toDouble() / 100.0).toInt()
