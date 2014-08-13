package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import com.epam.starwors.galaxy.Planet
import org.wow.logger.World
import org.wow.evaluation.transition.Transition
import org.apache.mahout.math.DenseVector
import org.apache.mahout.math.WeightedVector


public data class PlanetState(val world: World, val planet: Planet)

public data class PlanetTransition(val from: PlanetState, val to: PlanetState)

public data class FeatureExtractor(val eval: (PlanetState) -> Double, val weight: Double)

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
        return featuresExtractors.withIndices()
                .fold<Pair<Int, FeatureExtractor>, Vector>(DenseVector(featuresExtractors.size),
                        { (acc, extractor) -> acc.set(extractor.first, extractor.second.eval(input)); WeightedVector(acc, extractor.second.weight, extractor.first)  })
    }
}
