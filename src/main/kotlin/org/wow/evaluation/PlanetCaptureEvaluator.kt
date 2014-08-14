package org.wow.evaluation

import org.wow.logger.GameTurn

/**
 *  Considers only captured captured planets when evaluates user's power
 */
public class PlanetCaptureEvaluator : Evaluator {

    private val coefficient = 2.5

    override fun evaluate(playerName: String, world: GameTurn): Double {
        return coefficient * world.planets.filter { it.getOwner().equals(playerName) }.size
    }
}
