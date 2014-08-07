package org.wow.evaluation

import org.wow.logger.World

/**
 *
 */
public class Evaluator() {

    private val unitsCoefficient = 0.3
    private val planetsCoefficient = 0.3
    private val regenerationRateCoefficient = 0.3

    fun evaluatePlayerPower(playerName: String, world: World): Double {
        var units = 0
        var planets = 0
        var regenerationRate = 0
        var playersPlanets = world.planets!!.filter { it.getOwner().equals(playerName) }
        playersPlanets.forEach {
            units += it.getUnits()
            planets++
            regenerationRate += it.getType()!!.getIncrement()
        }
        regenerationRate /= planets
        return unitsCoefficient * units + planetsCoefficient * planets + regenerationRateCoefficient * regenerationRate
    }

}