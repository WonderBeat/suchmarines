package org.wow.evaluation.transition
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import org.wow.evaluation.UserPowerEvaluator
import org.wow.logger.GameTurn
import spock.lang.Specification
/**
 *
 */
class BestTransitionsFinderTest extends Specification {
    def 'same world'() {
        given:
        def evaluator = new UserPowerEvaluator();
        def planet = new Planet("12", "player", 100, PlanetType.TYPE_D, Collections.emptyList())
        def world = new GameTurn([planet], [])
        def game = [ world, world ]

        when:
        def transitions = new BestTransitionsFinder(evaluator).findBestTransitions(game)

        then:
        assert transitions != null
        assert transitions.size() == 0, "Same state with no regeneration is not a best move"
    }

    def 'different worlds'() {
        given:
        def evaluator = new UserPowerEvaluator();
        def planet1 = new Planet("12", "player", 100, PlanetType.TYPE_D, Collections.emptyList())
        def world1 = new GameTurn([planet1], [])
        def planet2 = new Planet("12", "player", 500, PlanetType.TYPE_D, Collections.emptyList())
        def world2 = new GameTurn([planet2], [])
        def game = [world1, world2]

        when:
        def transitions = new BestTransitionsFinder(evaluator).findBestTransitions(game)

        then:
        assert transitions != null
        assert transitions.size() == 1
    }
}
