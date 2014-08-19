package org.wow.learning.predict

import com.epam.starwors.galaxy.Planet
import org.apache.mahout.math.Vector
import org.wow.learning.vectorizers.Vectorizer
import org.wow.learning.categorizers.InOutMove
import org.wow.learning.categorizers.inOutMoveFromInt

public class InOutPlanetPredictor(val classifier: (Vector) -> Vector,
                                  val planetVectorizer: Vectorizer<Planet, Vector>
                                  ): Predictor<InOutMove, Planet> {


    override fun predict(input: Planet, world: Collection<Planet>): InOutMove {
        val vector = planetVectorizer.vectorize(input)
        val classification = classifier(vector)
        val maxValue = classification.maxValueIndex()
        return inOutMoveFromInt(maxValue)
    }

}
