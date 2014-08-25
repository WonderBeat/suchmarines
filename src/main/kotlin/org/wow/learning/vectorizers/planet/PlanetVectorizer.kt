package org.wow.learning.vectorizers.planet

import com.epam.starwors.galaxy.Planet
import org.wow.evaluation.transition.PlayerGameTurn
import org.wow.logger.PlayerMove
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder
import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.mahout.math.RandomAccessSparseVector

public data class PlanetTransition(val from: Planet,
                                   val to: Planet,
                                   val moves: List<PlayerMove>)

data class FeatureExtractor(val encoder: FeatureVectorEncoder,val eval: (Planet) -> Double, val weight: Double)

fun planetMoves(transition: PlayerGameTurn): List<PlanetTransition> {
    val nonSymmetricMoves = transition.from.moves.fold(listOf<PlayerMove>(), {(acc, move) ->
        if(transition.from.moves.any { it.from == move.to &&
                it.to == move.from && it.unitCount > move.unitCount })
            acc else acc.plus(move)
    })
    val planetsThatMoves = nonSymmetricMoves.fold(listOf<String>(), { (acc, item) ->
        acc.plus(item.from).plus(item.to)}).toSet()
    return transition.from.planets
            .filter { it.getOwner() == transition.playerName  }
            .filter { planetsThatMoves.contains(it.getId()) }
            .map { sourcePlanet -> PlanetTransition(sourcePlanet,
                    transition.to.planets.first { it.getId() == sourcePlanet.getId() },
                    transition.from.moves) }
}


public class PlanetVectorizer(private val featuresExtractors: List<FeatureExtractor>) : Vectorizer<Planet,
        Vector> {

    override fun vectorize(input: Planet): Vector {
        return featuresExtractors.withIndices()
                .fold<Pair<Int, FeatureExtractor>, Vector>(RandomAccessSparseVector(featuresExtractors.size),
                        { (acc, extractor) ->
                            acc.set(extractor.first, extractor.second.eval(input) * extractor.second.weight); acc })
    }
}
