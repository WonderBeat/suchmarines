package org.wow.evaluation.transition

import org.wow.logger.World
import org.wow.evaluation.Evaluator

/**
 *
 */
public class BestTransitionsFinder(val evaluator: Evaluator) {
    fun findBestTransitions(game: Collection<World>): Collection<Transition> {
        val players = game.first().planets!!.map { it.getOwner() }.filterNot { it!!.isEmpty() }.distinct()
        val transitions = pairs(game).map { pair ->
            players.map { player ->
                Transition(pair.first, pair.second, player!!)
            }
        }
        return transitions.map {
            it.maxBy { evaluator.difference(it.playerName, it.sourceWorld, it.resultWorld) }!! }
    }

    private fun pairs(game: Collection<World>): List<Pair<World, World>> = game.take(game.size - 1).zip(game.drop(1))
}
