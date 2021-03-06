package org.wow.evaluation

import org.wow.logger.GameTurn

/**
 *  Considers some different values with coefficients when evaluates player's power.
 *  Now it considers total player's units amount, player's planets amount and average (by palnets) regeneration rate.
 *  Considered values and coefficients may be changed
 */
public class UserPowerEvaluator : Evaluator {

    private val unitsCoefficient = 0.2
    private val planetsCoefficient = 0.8

    override fun evaluate(playerName: String, world: GameTurn): Double {
        var units: Double = 0.0
        var planets: Double = 0.0
        var maxPossibleUnits = world.planets.fold(0, { (acc, planet) -> acc + planet.getType()!!.getLimit() })
        var maxPossiblePlanets = world.planets.size()
        var playersPlanets = world.planets.filter { it.getOwner() == playerName }
        playersPlanets.forEach {
            units += it.getUnits()
            planets++
        }
        return unitsCoefficient * units / maxPossibleUnits + planetsCoefficient * planets / maxPossiblePlanets
    }

}
