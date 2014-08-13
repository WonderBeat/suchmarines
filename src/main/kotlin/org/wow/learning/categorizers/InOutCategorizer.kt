package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition

private val estimatorsList = listOf(::planetWasCaptured, ::planetMadeNoMove, ::planetSurroundedByNeutrals,
        ::planetSurroundedByNoEnemies, ::planetSurroundedByNoFriends,
        ::planetWasCapturedByCurrentPlanet, ::frontLiner, ::noPlanetsWereCaptured, ::isolatedPlanet)

fun inOutCategorizer(transition: PlanetTransition): Int {
    val move = estimatorsList.first { it.invoke(transition) != UndefinedMove }(transition)
    return when {
        move is InOutMove -> move.toInt()
        else -> throw UnsupportedOperationException("Can't transform move to integer")
    }
}
