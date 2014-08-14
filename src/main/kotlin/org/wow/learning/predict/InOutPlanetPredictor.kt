package org.wow.learning.predict

import com.epam.starwors.galaxy.Planet
import org.apache.mahout.math.Vector
import org.wow.learning.vectorizers.Vectorizer
import org.wow.learning.vectorizers.planet.PlanetState
import org.wow.learning.categorizers.InOutMove

public class InOutPlanetPredictor(val classifier: (Vector) -> Vector,
                                  val planetVectorizer: Vectorizer<PlanetState, Vector>
                                  ): Predictor<InOutMove, Planet> {


    override fun predict(input: Planet, world: Collection<Planet>): InOutMove {
        val vector = planetVectorizer.vectorize(PlanetState(world, input))
        val classification = classifier(vector)
        val maxValue = classification.maxValueIndex()
        return org.wow.learning.categorizers.inOutMoveFromInt(maxValue)
    }

}
