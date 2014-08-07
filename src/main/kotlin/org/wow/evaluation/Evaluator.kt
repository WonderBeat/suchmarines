package org.wow.evaluation

import org.wow.logger.World

/**
 *
 */
public trait Evaluator {
    fun evaluate(playerName: String, world: World): Double
}