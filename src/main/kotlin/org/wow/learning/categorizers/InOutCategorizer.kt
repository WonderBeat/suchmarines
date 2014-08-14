package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.percentUsers

fun inOutCategorizer(transition: PlanetTransition): Int {
    val attackMoves = transition.moves.filter { it.from == transition.from.getId() }
    val defendMoves = transition.moves.filter { it.to == transition.from.getId() }
    val unitsMoved = Math.abs(transition.to.getUnits() - transition.from.getUnits())
    return when {
        attackMoves.size > 0 -> InOutMove(out = transition.from.percentUsers(unitsMoved)).toInt()
        defendMoves.size > 0 -> InOutMove(`in` = transition.from.percentUsers(unitsMoved)).toInt()
        else -> InOutMove().toInt()
    }
}
