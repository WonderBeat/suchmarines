package org.wow.learning

import org.wow.logger.World
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
fun Planet.friendsAround(): Int {
    val allUnits = this.getNeighbours()!!.sumUnits()
    val friendUnits = this.friendsNeighbours().sumUnits()
    return when {
        allUnits == 0 -> 100
        else -> 100 * friendUnits / allUnits
    }
}

/**
 * Enemies around percentage
 */
fun Planet.enemiesAround(): Int = 100 - this.friendsAround()

/**
 * Planet power
 *
 * All planets power = 100%
 * Current planet = ?
 */
fun Planet.planetPower(world: World): Int
    = 100 * absolutePlanetPower(this) / world.planets!!.fold(0) { acc, it -> acc + absolutePlanetPower(it) }

private fun absolutePlanetPower(planet: Planet): Int
        = planet.getUnits() + 10 * planet.getType()!!.getIncrement() + planet.getType()!!.getLimit()


/**
 * Isolation level %
 * enemy num / friends num
 */
fun Planet.isolationLevel(): Int = this.enemiesAround() / this.friendsAround()

fun Planet.unitsAfterRegeneration(): Int {
    val expected = (this.getUnits() + this.getType()!!.getIncrement() * 0.01 *
            this.getUnits()).toInt()
    return when {
        expected > this.getType()!!.getLimit() -> this.getType()!!.getLimit()
        else -> expected
    }
}

fun Planet.percentUsers(percent: Int): Int = ((this.getUnits() * percent).toDouble() / 100.0).toInt()
