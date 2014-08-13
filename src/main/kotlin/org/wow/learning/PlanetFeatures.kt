package org.wow.learning

import org.wow.logger.World
import com.epam.starwors.galaxy.Planet

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

fun Planet.volumePercentage(): Double = this.getUnits() * 100.0 / this.getType()!!.getLimit()


/**
 * Planet power
 */
fun Planet.planetPower(world: World): Double
    = this.getUnits().toDouble()

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
