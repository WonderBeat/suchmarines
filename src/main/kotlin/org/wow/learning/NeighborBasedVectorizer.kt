package org.wow.learning

import org.apache.mahout.math.Vector
import org.apache.mahout.math.RandomAccessSparseVector
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder
import org.wow.evaluation.Transition

public class NeighborBasedVectorizer: Vectorizer {

    val enemiesAround = ContinuousValueEncoder("enemies")
    val friendsAround = ContinuousValueEncoder("friend")
    val planetPower = ContinuousValueEncoder("power")

    override fun vectorize(move: Transition): List<Vector> =
        move.from.planets!!.filter { it.getOwner() == move.user }.map { planet ->
            val vector = RandomAccessSparseVector()
            enemiesAround.addToVector(null: ByteArray?, planet.enemiesAround(move.from).toDouble(), vector)
            friendsAround.addToVector(null: ByteArray?, planet.friendsAround(move.from).toDouble(), vector)
            planetPower.addToVector(null: ByteArray?, planet.planetPower(move.from).toDouble(), vector)
            vector
        }
}
