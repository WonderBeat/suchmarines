package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.mahout.math.RandomAccessSparseVector
import com.epam.starwors.galaxy.Planet
import org.wow.logger.World
import org.wow.learning.enemiesAround
import org.wow.learning.friendsAround
import org.wow.learning.planetPower
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder


public data class PlanetState(val world: World, val planet: Planet)

public data class PlanetTransition(val from: PlanetState, val to: PlanetState)

public data class FeatureExtractor(val encoder: FeatureVectorEncoder, val weightEvaluator: (PlanetState) -> Double)


public class PlanetVectorizer(private val featuresExtractors: List<FeatureExtractor>) : Vectorizer<PlanetState,
        Vector> {

    override fun vectorize(input: PlanetState): Vector {
        val vector = RandomAccessSparseVector(featuresExtractors.size)
        featuresExtractors.forEach { it.encoder.addToVector(null: ByteArray?, it.weightEvaluator(input), vector) }
        return vector
    }
}
