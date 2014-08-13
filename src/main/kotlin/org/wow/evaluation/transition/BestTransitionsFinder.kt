package org.wow.evaluation.transition

import org.wow.logger.World
import org.wow.evaluation.Evaluator


public data class Transition(val sourceWorld: World, val resultWorld: World, val playerName: String)

public class BestTransitionsFinder(val evaluator: Evaluator) {
    fun findBestTransitions(game: List<World>): Collection<Transition> {
        val players = listPlayersOnMap(game)
        val transitions = pairs(game).map { pair ->
            players.map { player ->
                Transition(pair.first, pair.second, player!!)
            }
        }
        val best = transitions.map {
            it.maxBy { evaluator.difference(it.playerName, it.sourceWorld, it.resultWorld) }!!
        }
        return best
    }

    fun listPlayersOnMap(game: List<World>): Set<String?> {
        return game.first().planets!!.map { it.getOwner() }.filterNot { it!!.isEmpty() }.distinct()
    }

    /**
     * Creates World pairs
     * (first, second), (second, third) ... (n-1, n)
     */
    private fun pairs(game: List<World>): List<Pair<World, World>> = game.take(game.size - 1).zip(game.tail)
}
