package org.wow.evaluation

import org.wow.evaluation.transition.PlayerGameTurn
import org.wow.logger.GameTurn

/**
 *
 */
public trait Evaluator {
    fun evaluate(playerName: String, world: GameTurn): Double

    fun difference(playerName: String, first: GameTurn, second: GameTurn): Double =
        evaluate(playerName, second) - evaluate(playerName, first)
}
