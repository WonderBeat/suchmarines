package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.mahout.math.RandomAccessSparseVector
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder
import com.epam.starwors.galaxy.Planet
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder
import org.wow.logger.World
import org.wow.learning.enemiesAround
import org.wow.learning.friendsAround
import org.wow.learning.planetPower


public data class PlanetState(val world: World, val planet: Planet)

public data class PlanetTransition(val from: PlanetState, val to: PlanetState)


public class NeighborBasedPlanetVectorizer : Vectorizer<PlanetState, Vector> {

    val enemiesAround = ContinuousValueEncoder("enemies")
    val friendsAround = ContinuousValueEncoder("friend")
    val planetPower = ContinuousValueEncoder("power")
    var planetSize = ConstantValueEncoder("size")

    override fun vectorize(input: PlanetState): Vector {
        val vector = RandomAccessSparseVector(4)
        enemiesAround.addToVector(null: ByteArray?, input.planet.enemiesAround(input.world).toDouble(), vector)
        friendsAround.addToVector(null: ByteArray?, input.planet.friendsAround(input.world).toDouble(), vector)
        planetPower.addToVector(null: ByteArray?, input.planet.planetPower(input.world).toDouble(), vector)
        planetSize.addToVector(null: ByteArray?, input.planet.getType()!!.getLimit().toDouble(), vector)
        return vector
    }
}
