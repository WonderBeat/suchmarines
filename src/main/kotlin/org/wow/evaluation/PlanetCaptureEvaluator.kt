package org.wow.evaluation

import com.epam.starwors.galaxy.Planet

/**
 *  Considers only captured captured planets when evaluates user's power
 */
public class PlanetCaptureEvaluator : Evaluator {

    private val coefficient = 2.5

    override fun evaluate(playerName: String, world: Collection<Planet>): Double {
        return coefficient * world.filter { it.getOwner().equals(playerName) }.size
    }
}
