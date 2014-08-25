package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition
import com.epam.starwors.galaxy.Planet
import org.wow.learning.enemiesNeighbours
import org.wow.learning.friendsNeighbours

private fun Planet.percentFromPlanetUnits(units: Int): Int = (units * 100.0 / this.getUnits().toDouble()).toInt()

fun inOutCategorizer(transition: PlanetTransition): Int {
    val outMoves = transition.moves.filter { it.from == transition.from.getId() }
    val inMoves = transition.moves.filter { it.to == transition.from.getId() }
    val inSupportMoves = inMoves.filter { move ->
        transition.from.getNeighbours()!!
                .filter { it.getId() == move.from }
                .all { it.getOwner() == transition.from.getOwner() }
    }
    val outUnits = outMoves.fold(0, { (acc, move) -> acc + move.unitCount })
    val inSupportUnits = inSupportMoves.fold(0, { (acc, move) -> acc + move.unitCount })
    val planetWasCaptured = transition.from.getOwner() != transition.to.getOwner()
    return when {
        planetWasCaptured -> InOutMove(`in`= 100).toInt()
        outUnits > 0 -> InOutMove(out = transition.from.percentFromPlanetUnits(outUnits)).toInt()
        inSupportUnits > 0 -> InOutMove(`in` = transition.from.percentFromPlanetUnits(inSupportUnits)).toInt()
        else -> InOutMove().toInt()
    }
}
