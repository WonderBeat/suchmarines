package org.wow.learning

import org.wow.logger.World
import com.epam.starwors.galaxy.Planet

fun Planet.enemiesNeighbours(): List<Planet> = this.getNeighbours()!!.filter { it.getOwner() != this.getOwner() && it
        .getOwner().isNotEmpty() }

fun Planet.friendsNeighbours(): List<Planet> = this.getNeighbours()!!.filter { it.getOwner() == this.getOwner() }

/**
* Big enemies Planet around
* */
/**
 * Enemies around percentage
 */
fun Planet.enemiesAround(): Int {
    val allUnits = this.getNeighbours()!!.sumUnits() + this.getUnits()
    val enemyUnits = this.enemiesNeighbours().sumUnits()
    return 100 * enemyUnits / allUnits
}

fun Collection<Planet>.sumUnits(): Int = this.fold(0) { acc, it -> acc + it.getUnits() }

/**
 * Enemies around percentage
 */
fun Planet.friendsAround(): Int {
    val allUnits = this.getNeighbours()!!.sumUnits() + this.getUnits()
    val friendUnits = this.friendsNeighbours().sumUnits() + this.getUnits()
    return 100 * friendUnits / allUnits
}

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
