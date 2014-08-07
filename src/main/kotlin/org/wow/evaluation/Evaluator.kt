package org.wow.evaluation

import org.wow.logger.World

/**
 *
 */
public trait Evaluator {
    fun evaluate(playerName: String, world: World): Double

    fun difference(playerName: String, first: World, second: World): Double =
            evaluate(playerName, second) - evaluate(playerName, first)
}