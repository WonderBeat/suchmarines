package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition
import com.epam.starwors.galaxy.Planet

private fun Planet.percentFromPlanetUnits(units: Int): Int = (units * 100.0 / this.getUnits()).toInt()

fun inOutCategorizer(transition: PlanetTransition): Int {
    val outMoves = transition.moves.filter { it.from == transition.from.getId() }
    val inMoves = transition.moves.filter { it.to == transition.from.getId() }
    val outUnits = outMoves.fold(0, { (acc, move) -> acc + move.unitCount })
    val inUnits = inMoves.fold(0, { (acc, move) -> acc + move.unitCount })
    val planetWasCaptured = transition.from.getOwner() != transition.to.getOwner()
    return when {
        planetWasCaptured -> InOutMove(`in`= 100).toInt()
        outUnits > 0 -> InOutMove(out = transition.from.percentFromPlanetUnits(outUnits)).toInt()
        inUnits > 0 -> InOutMove(`in` = transition.from.percentFromPlanetUnits(inUnits)).toInt()
        else -> InOutMove().toInt()
    }
}
