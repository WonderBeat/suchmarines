package org.wow.evaluation

import org.wow.logger.GameTurn
import com.epam.starwors.galaxy.Planet

/**
 *
 */
public trait Evaluator {
    fun evaluate(playerName: String, world: Collection<Planet>): Double

    fun difference(playerName: String, first: Collection<Planet>, second: Collection<Planet>): Double =
            evaluate(playerName, second) - evaluate(playerName, first)
}
