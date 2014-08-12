package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition

/**
 * Mahaut classifier accepts int numbers only
 * So lets convert out move to num
 * 100 - 0% in 0% out
 * 0 - 100% in 0% out
 * 200 - 0%in 100% out
 */
fun InOutMove.toInt(): Int = when {
    this.`in` > 100 -> 100
    this.out > 100 -> 0
    this.`in` > 0 -> 100 - this.`in`
    else -> 100 + out
}

fun inOutMoveFromInt(num: Int): InOutMove = when {
    num >=100 -> InOutMove(0, num - 100)
    else -> InOutMove(100 - num, 0)
}

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
