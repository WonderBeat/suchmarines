package org.wow.evaluation

import org.wow.logger.World

/**
 *  Considers some different values with coefficients when evaluates player's power.
 *  Now it considers total player's units amount, player's planets amount and average (by palnets) regeneration rate.
 *  Considered values and coefficients may be changed
 */
public class UserPowerEvaluator : Evaluator {

    private val unitsCoefficient = 0.1
    private val planetsCoefficient = 0.6
    private val regenerationRateCoefficient = 0.3

    override fun evaluate(playerName: String, world: World): Double {
        var units: Double = 0.0
        var planets: Double = 0.0
        var regenerationRate: Double = 0.0
        var maxPossibleUnits = world.planets!!.fold(0, { (acc, planet) -> acc + planet.getType()!!.getLimit() })
        var maxPossiblePlanets = world.planets.size()
        var maxRegenRate = world.planets.fold(0, { (acc, planet) -> acc + planet.getType()!!.getIncrement() })
        var playersPlanets = world.planets.filter { it.getOwner().equals(playerName) }
        playersPlanets.forEach {
            units += it.getUnits()
            planets++
            regenerationRate += if(it.getUnits() < it.getType()!!.getIncrement()) it.getType()!!.getIncrement() else 0
        }
        val score = unitsCoefficient * units / maxPossibleUnits + planetsCoefficient * planets / maxPossiblePlanets +
                regenerationRateCoefficient * regenerationRate / maxRegenRate
        return score
    }

}
