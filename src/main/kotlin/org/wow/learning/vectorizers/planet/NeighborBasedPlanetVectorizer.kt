package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.mahout.math.RandomAccessSparseVector
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder
import com.epam.starwors.galaxy.Planet
import org.wow.learning.enemiesAround
import org.wow.learning.planetPower
import org.wow.learning.friendsAround


public data class PlanetTransition(val transition: org.wow.learning.vectorizers.Transition, val planet: Planet)

public class NeighborBasedPlanetVectorizer : Vectorizer<PlanetTransition, Vector> {

    val enemiesAround = ContinuousValueEncoder("enemies")
    val friendsAround = ContinuousValueEncoder("friend")
    val planetPower = ContinuousValueEncoder("power")

    override fun vectorize(input: PlanetTransition): Vector {
        val vector = RandomAccessSparseVector()
        enemiesAround.addToVector(null: ByteArray?, input.planet.enemiesAround(input.transition.from).toDouble(), vector)
        friendsAround.addToVector(null: ByteArray?, input.planet.friendsAround(input.transition.from).toDouble(), vector)
        planetPower.addToVector(null: ByteArray?, input.planet.planetPower(input.transition.from).toDouble(), vector)
        return vector
    }
}
