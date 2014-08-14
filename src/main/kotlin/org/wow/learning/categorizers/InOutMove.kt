package org.wow.learning.categorizers

import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.enemiesAroundPercentage
import org.wow.learning.friendsAroundPercentage
import org.wow.learning.enemiesNeighbours
import org.wow.learning.unitsAfterRegeneration
import org.wow.learning.friendsNeighbours
import org.wow.learning.neutralNeighbours
import org.wow.learning.planetPower
import org.wow.learning.isolationLevel

trait Move

object UndefinedMove: Move

data class InOutMove(val `in`: Int = 0, val out: Int = 0): Move

/**
 * Mahaut classifier accepts int numbers only
 * So lets convert out move to num
 * 100 - 0% in 0% out
 * 0 - 100% in 0% out
 * 200 - 0%in 100% out
 */
fun InOutMove.toInt(): Int = when {
    this.`in` > 100 -> 0
    this.out > 100 -> 200
    this.`in` > 0 -> 100 - this.`in`
    else -> 100 + out
}

fun inOutMoveFromInt(num: Int): InOutMove = when {
    num >= 100 -> InOutMove(0, num - 100)
    else -> InOutMove(100 - num, 0)
}
