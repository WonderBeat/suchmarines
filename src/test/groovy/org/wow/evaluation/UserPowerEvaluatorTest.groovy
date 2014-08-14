package org.wow.evaluation
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import org.wow.logger.GameTurn
import spock.lang.Specification
/**
 *
 */
class UserPowerEvaluatorTest extends Specification {


    def 'evaluator evaluates some value'() {
        given:
        def evaluator = new UserPowerEvaluator();

        when:
        def planet = new Planet("12", "player", 100, PlanetType.TYPE_D, Collections.emptyList())
        def power = evaluator.evaluate("player", new GameTurn([planet], []))

        then:
        assert power > 0
    }
}
