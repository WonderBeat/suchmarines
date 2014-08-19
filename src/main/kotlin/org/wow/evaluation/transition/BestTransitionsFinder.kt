package org.wow.evaluation.transition

import org.wow.logger.GameTurn
import org.wow.evaluation.Evaluator


public data class PlayerGameTurn(val from: GameTurn,
                                 val to: GameTurn,
                                 val playerName: String)

public class BestTransitionsFinder(val evaluator: Evaluator) {
    fun findBestTransitions(game: List<GameTurn>): List<PlayerGameTurn> {
        val players = listPlayersOnMap(game)
        val best = pairs(game).map { gameTurnPair ->
            val playersTurns = players.map { player ->
                PlayerGameTurn(gameTurnPair.first, gameTurnPair.second, player!!)
            }
            findBestPlayerTurn(playersTurns)
        }.filterNotNull()
        return best
    }

    fun findBestPlayerTurn(turns: List<PlayerGameTurn>): PlayerGameTurn? =
        turns.map { Pair(evaluateTurn(it), it) }
             .filter { it.first > 0 }
             .sortBy { it.first }
             .last?.second

    fun evaluateTurn(turn: PlayerGameTurn): Double = evaluator.difference(turn.playerName, turn.from, turn.to)

    fun listPlayersOnMap(game: List<GameTurn>): Set<String?> {
        return game.first().planets.map { it.getOwner() }.filterNot { it!!.isEmpty() }.distinct()
    }

    /**
     * Creates World pairs
     * (first, second), (second, third) ... (n-1, n)
     */
    private fun pairs(game: List<GameTurn>): List<Pair<GameTurn, GameTurn>> = game.take(game.size - 1).zip(game.tail)
}
