package org.wow.learning.categorizers
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.logger.GameTurn
import spock.lang.Specification

class InOutMoveTest extends Specification {

    def 'InOut move convertors test'() {
        given:
        def move = new InOutMove(moveIn, moveOut)

        when:
        def mahoutClass = org.wow.learning.categorizers.CategorizersPackage.toInt(move)
        def moveRestored = org.wow.learning.categorizers.CategorizersPackage.inOutMoveFromInt(mahoutClass)

        then:
        assert mahoutClass == goal
        assert moveRestored.in == move.in || moveRestored.in == 100 && move.in > 100
        assert moveRestored.out == moveRestored.out || moveRestored.out == 100 && move.out > 100

        where:
        moveIn | moveOut | goal
        10  | 0 | 90
        100 | 0 | 0
        0   | 10 | 110
        0   | 90 | 190
        110 | 0 | 0
        0   | 150 | 200
    }

}
