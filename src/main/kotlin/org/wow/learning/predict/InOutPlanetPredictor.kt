package org.wow.learning.predict

import com.epam.starwors.galaxy.Planet
import org.apache.mahout.math.Vector


data class PlanetMovePrediction(val out: Int = 0, val `in`: Int = 0)

public class InOutPlanetPredictor(val classifier: (Vector) -> Vector,
                                  val planetVectorizer: org.wow.learning.vectorizers.Vectorizer<Planet, Vector>
                                  ): Predictor<PlanetMovePrediction, Planet> {


    override fun predict(input: Planet): PlanetMovePrediction {
        val vector = planetVectorizer.vectorize(input)
        val classification = classifier(vector)
        val maxValue = classification.maxValue()
        val usersPercentage = if(maxValue > 100) maxValue - 100 else 100 - maxValue
        val usersNumber = (input.getUnits().toDouble() * (usersPercentage / 100).toDouble()).toInt()
        return when {
            maxValue > 100 -> PlanetMovePrediction(out = usersNumber)
            else -> PlanetMovePrediction(`in` = usersNumber)
        }
    }
}
