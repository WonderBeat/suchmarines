package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.mahout.math.RandomAccessSparseVector
import com.epam.starwors.galaxy.Planet
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder
import org.wow.evaluation.transition.PlayerGameTurn
import org.wow.logger.PlayerMove

public data class PlanetTransition(val from: Planet,
                                   val to: Planet,
                                   val moves: List<PlayerMove>)

public data class FeatureExtractor(val encoder: FeatureVectorEncoder, val weightEvaluator: (Planet) -> Double)

/**
 * Transition contains 2 world states and player name
 * Planet transition contains 2 planet states in 2 worlds. Before and after move
 */
fun transitionToPlanetTransition(transition: PlayerGameTurn): List<PlanetTransition> =
        transition.from.planets
                .filter { it.getOwner() == transition.playerName  }
                .map { sourcePlanet -> PlanetTransition(sourcePlanet,
                        transition.to.planets.first { it.getId() == sourcePlanet.getId() },
                        transition.from.moves) }

public class PlanetVectorizer(private val featuresExtractors: List<FeatureExtractor>) : Vectorizer<Planet,
        Vector> {

    override fun vectorize(input: Planet): Vector {
        val vector = RandomAccessSparseVector(featuresExtractors.size)
        featuresExtractors.forEach { it.encoder.addToVector(null: ByteArray?, it.weightEvaluator(input), vector) }
        return vector
    }
}
