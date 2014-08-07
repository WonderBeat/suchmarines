package org.wow.evaluation

import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import org.wow.logger.World
import spock.lang.Specification

/**
 *
 */
class PlanetCaptureEvaluatorTest extends Specification {
    def 'evaluator evaluates some value'() {
        given:
        def evaluator = new UserPowerEvaluator();

        when:
        def planet = new Planet("12", "player", 100, PlanetType.TYPE_D, Collections.emptyList())
        def world = new World([planet])
        def power = evaluator.evaluate("player", world)

        then:
        assert power > 0
    }
}
