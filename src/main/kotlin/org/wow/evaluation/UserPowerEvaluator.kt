package org.wow.evaluation

import org.wow.logger.World

/**
 *  Considers some different values with coefficients when evaluates player's power.
 *  Now it considers total player's units amount, player's planets amount and average (by palnets) regeneration rate.
 *  Considered values and coefficients may be changed
 */
public class UserPowerEvaluator : Evaluator {

    private val unitsCoefficient = 0.3
    private val planetsCoefficient = 0.3
    private val regenerationRateCoefficient = 0.3

    override fun evaluate(playerName: String, world: World): Double {
        var units = 0
        var planets = 0
        var regenerationRate = 0
        var playersPlanets = world.planets!!.filter { it.getOwner().equals(playerName) }
        playersPlanets.forEach {
            units += it.getUnits()
            planets++
            regenerationRate += it.getType()!!.getIncrement()
        }
        return unitsCoefficient * units + planetsCoefficient * planets + regenerationRateCoefficient * regenerationRate
    }

}
