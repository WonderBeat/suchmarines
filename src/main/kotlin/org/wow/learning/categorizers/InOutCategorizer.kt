package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition

private val estimatorsList = listOf(::planetWasCaptured, ::planetMadeNoMove, ::planetSurroundedByNeutrals,
        ::allEnemiesStronger,
        ::planetSurroundedByNoEnemies, ::planetSurroundedByNoFriends, ::planetWasCapturedByCurrentPlanet , ::noPlanetsWereCaptured)

fun inOutCategorizer(transition: PlanetTransition): Int {
    val move = estimatorsList.map { it.invoke(transition) }.filter { it != UndefinedMove }.first
    return when {
        move is InOutMove -> move.toInt()
        else -> throw UnsupportedOperationException("Can't transform move to integer")
    }
}
