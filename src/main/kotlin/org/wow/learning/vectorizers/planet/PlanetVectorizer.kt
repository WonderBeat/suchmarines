package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.mahout.math.RandomAccessSparseVector
import com.epam.starwors.galaxy.Planet
import org.wow.logger.World
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder
import org.wow.evaluation.transition.Transition


public data class PlanetState(val world: World, val planet: Planet)

public data class PlanetTransition(val from: PlanetState, val to: PlanetState)

public data class FeatureExtractor(val encoder: FeatureVectorEncoder, val weightEvaluator: (PlanetState) -> Double)

/**
 * Transition contains 2 world states and player name
 * Planet transition contains 2 planet states in 2 worlds. Before and after move
 */
fun transitionToPlanetTransition(transition: Transition): List<PlanetTransition> =
        transition.sourceWorld.planets!!
                .filter { it.getOwner() == transition.playerName  }
                .map { sourcePlanet -> PlanetTransition(PlanetState(transition.sourceWorld, sourcePlanet),
                        PlanetState(transition.resultWorld, transition.resultWorld.planets!!.first { it.getId() == sourcePlanet.getId()})) }

public class PlanetVectorizer(private val featuresExtractors: List<FeatureExtractor>) : Vectorizer<PlanetState,
        Vector> {

    override fun vectorize(input: PlanetState): Vector {
        val vector = RandomAccessSparseVector(featuresExtractors.size)
        featuresExtractors.forEach { it.encoder.addToVector(null: ByteArray?, it.weightEvaluator(input), vector) }
        return vector
    }
}
