package org.wow.learning.predict

import com.epam.starwors.galaxy.Planet
import org.apache.mahout.math.Vector
import org.wow.learning.vectorizers.Vectorizer
import org.wow.logger.World
import org.wow.learning.vectorizers.planet.PlanetState
import org.wow.learning.inOutMoveFromInt


/**
 * Percent of your users, that should be moved in or out planet
 */
data class PlanetMovePrediction(val out: Int = 0, val `in`: Int = 0)

public class InOutPlanetPredictor(val classifier: (Vector) -> Vector,
                                  val planetVectorizer: Vectorizer<PlanetState, Vector>
                                  ): Predictor<PlanetMovePrediction, Planet> {


    override fun predict(input: Planet, world: World): PlanetMovePrediction {
        val vector = planetVectorizer.vectorize(PlanetState(world, input))
        val classification = classifier(vector)
        val maxValue = classification.maxValueIndex()
        val move = inOutMoveFromInt(maxValue)
        return when {
            move.out > 0 -> PlanetMovePrediction(out = countUserNumber(move.out, input))
            else -> PlanetMovePrediction(`in` = countUserNumber(move.`in`, input))
        }
    }

    fun countUserNumber(percent: Int, planet: Planet): Int =
        (planet.getUnits().toDouble() * (percent.toDouble() / 100).toDouble()).toInt()

}
