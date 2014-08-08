package org.wow.learning.predict

import com.epam.starwors.galaxy.Planet
import org.apache.mahout.math.Vector
import org.wow.learning.vectorizers.Vectorizer
import org.wow.logger.World
import org.wow.learning.vectorizers.planet.PlanetState


data class PlanetMovePrediction(val out: Int = 0, val `in`: Int = 0)

public class InOutPlanetPredictor(val classifier: (Vector) -> Vector,
                                  val planetVectorizer: Vectorizer<PlanetState, Vector>
                                  ): Predictor<PlanetMovePrediction, Planet> {


    override fun predict(input: Planet, world: World): PlanetMovePrediction {
        val vector = planetVectorizer.vectorize(PlanetState(world, input))
        val classification = classifier(vector)
        val maxValue = classification.maxValueIndex()
        val usersPercentage = if(maxValue >= 100) maxValue - 100 else 100 - maxValue
        val usersNumber = (input.getUnits().toDouble() * (usersPercentage.toDouble() / 100).toDouble()).toInt()
        return when {
            maxValue > 100 -> PlanetMovePrediction(out = usersNumber)
            else -> PlanetMovePrediction(`in` = usersNumber)
        }
    }
}
