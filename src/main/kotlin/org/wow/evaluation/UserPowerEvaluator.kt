package org.wow.evaluation

import com.epam.starwors.galaxy.Planet

/**
 *  Considers some different values with coefficients when evaluates player's power.
 *  Now it considers total player's units amount, player's planets amount and average (by palnets) regeneration rate.
 *  Considered values and coefficients may be changed
 */
public class UserPowerEvaluator : Evaluator {

    private val unitsCoefficient = 0.5
    private val planetsCoefficient = 0.2
    private val regenerationRateCoefficient = 0.3

    override fun evaluate(playerName: String, world: Collection<Planet>): Double {
        var units: Double = 0.0
        var planets: Double = 0.0
        var regenerationRate: Double = 0.0
        var maxPossibleUnits = world.fold(0, { (acc, planet) -> acc + planet.getType()!!.getLimit() })
        var maxPossiblePlanets = world.size()
        var maxRegenRate = world.fold(0, { (acc, planet) -> acc + planet.getType()!!.getIncrement() })
        var playersPlanets = world.filter { it.getOwner().equals(playerName) }
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
