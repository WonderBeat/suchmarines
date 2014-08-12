package org.wow.evaluation.learning.predict

import com.epam.starwors.galaxy.Planet
import kotlin.Function1
import org.apache.mahout.math.RandomAccessSparseVector
import org.wow.learning.categorizers.InOutMove
import org.wow.learning.predict.InOutPlanetPredictor
import org.wow.learning.vectorizers.Vectorizer
import org.wow.logger.World
import spock.lang.Specification

class InOutPredictorTest extends Specification {

    def 'defence required'() {
        given:
        def vector = Mock(org.apache.mahout.math.Vector)
        vector.maxValueIndex() >> classificationResult
        def predictor = new InOutPlanetPredictor({v -> vector} as Function1<org.apache.mahout.math.Vector, org.apache.mahout.math.Vector>,
                                                 {p -> new RandomAccessSparseVector(3)} as Vectorizer<Planet, org.apache.mahout.math.Vector>)

        when:
        def prediction = predictor.predict(new Planet(units: 1000), new World())

        then:
        assert prediction == expectation

        where:
        classificationResult | expectation
        100                  | new InOutMove()
        100                  | new InOutMove()
        120                  | new InOutMove(0, 20)
        30                   | new InOutMove(70, 0)
        55                   | new InOutMove(45, 0)

    }
}
