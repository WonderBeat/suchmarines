package org.wow.evaluation.learning.predict

import com.epam.starwors.galaxy.Planet
import kotlin.Function1
import org.apache.mahout.math.RandomAccessSparseVector
import org.wow.learning.Vectorizer
import org.wow.learning.predict.InOutPlanetPredictor
import org.wow.learning.predict.PlanetMovePrediction
import spock.lang.Specification


class InOutPredictorTest extends Specification {

    def 'defence required'() {
        given:
        def vector = Mock(org.apache.mahout.math.Vector)
        vector.maxValue() >> classificationResult
        def predictor = new InOutPlanetPredictor({v -> vector} as Function1<org.apache.mahout.math.Vector, org.apache.mahout.math.Vector>,
                                                 {p -> new RandomAccessSparseVector(3)} as Vectorizer<Planet, org.apache.mahout.math.Vector>)

        when:
        def prediction = predictor.predict(new Planet(units: 1000))

        then:
        assert prediction == expectation

        where:
        classificationResult | expectation
        100                  | new PlanetMovePrediction()
        100                  | new PlanetMovePrediction()
        120                  | new PlanetMovePrediction(200, 0)
        30                   | new PlanetMovePrediction(0, 700)
        55                   | new PlanetMovePrediction(0, 450)

    }
}