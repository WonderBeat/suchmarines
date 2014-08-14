package org.wow.evaluation.transition

import org.wow.logger.GameTurn
import org.wow.evaluation.Evaluator


public data class PlayerGameTurn(val from: GameTurn,
                                 val to: GameTurn,
                                 val playerName: String)

public class BestTransitionsFinder(val evaluator: Evaluator) {
    fun findBestTransitions(game: List<GameTurn>): List<PlayerGameTurn> {
        val players = listPlayersOnMap(game)
        val transitions = pairs(game).map { pair ->
            players.map { player ->
                PlayerGameTurn(pair.first, pair.second, player!!)
            }
        }
        return transitions.map {
            it.maxBy { evaluator.difference(it.playerName, it.from, it.to) }!! }
    }

    fun listPlayersOnMap(game: List<GameTurn>): Set<String?> {
        return game.first().planets.map { it.getOwner() }.filterNot { it!!.isEmpty() }.distinct()
    }

    /**
     * Creates World pairs
     * (first, second), (second, third) ... (n-1, n)
     */
    private fun pairs(game: List<GameTurn>): List<Pair<GameTurn, GameTurn>> = game.take(game.size - 1).zip(game.tail)
}
