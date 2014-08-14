package org.wow.evaluation.transition

import org.wow.logger.GameTurn
import org.wow.evaluation.Evaluator
import com.epam.starwors.galaxy.Planet


public data class Transition(val sourceWorld: Collection<Planet>, val resultWorld: Collection<Planet>,
                             val playerName: String)

public class BestTransitionsFinder(val evaluator: Evaluator) {
    fun findBestTransitions(game: List<GameTurn>): Collection<Transition> {
        val players = listPlayersOnMap(game)
        val transitions = pairs(game).map { pair ->
            players.map { player ->
                Transition(pair.first.planets, pair.second.planets, player!!)
            }
        }
        return transitions.map {
            it.maxBy { evaluator.difference(it.playerName, it.sourceWorld, it.resultWorld) }!! }
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
