package org.wow.learning.categorizers
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import org.wow.learning.vectorizers.planet.PlanetState
import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.logger.World
import spock.lang.Specification

class CategorizersTest extends Specification {

    Planet neutral() { new Planet('neutral', '', 0, PlanetType.TYPE_B, []) }
    Planet friend(String id) { new Planet(id, 'adolf', 10, PlanetType.TYPE_A, []) }
    Planet enemy(String id) { new Planet(id, 'mahmud', 10, PlanetType.TYPE_A, []) }

    def 'planet with no enemies and friends'() {
        given:
        def fromPlanet = friend('1')
        def toPlanet = friend('1')
        toPlanet.setUnits(25)

        def from = new World([fromPlanet])
        def to = new World([toPlanet])

        def transition = new PlanetTransition(new PlanetState(from, fromPlanet), new PlanetState(to, toPlanet))

        when:
        InOutMove move = org.wow.learning.categorizers.CategorizersPackage.estimateMoveWithUnitsDifference(transition)

        then:
        assert move.in > 0 && move.out == 0
        assert move.in == 140, 'units amount was increased by 140%. type A planet regen rate 10% ->  25 - (10 + 1) * 100 / 10 '
    }

    def 'planet was captured during transition'() {
        given:
        def fromPlanet = friend('1')
        def toPlanet = enemy('1')

        def from = new World([fromPlanet])
        def to = new World([toPlanet])

        def transition = new PlanetTransition(new PlanetState(from, fromPlanet), new PlanetState(to, toPlanet))

        when:
        InOutMove move = org.wow.learning.categorizers.CategorizersPackage.planetWasCaptured(transition)

        then:
        assert move.in == 100, 'request defence!!'
    }

    def 'planet made no move'() {
        given: 'planet state changed according regeneration rate'
        def fromPlanet = friend('1')
        def toPlanet = friend('1')
        toPlanet.setUnits((fromPlanet.getUnits() + fromPlanet.getUnits().toDouble() / 100 * fromPlanet.getType().increment).toInteger())

        def from = new World([fromPlanet])
        def to = new World([toPlanet])
        def transition = new PlanetTransition(new PlanetState(from, fromPlanet), new PlanetState(to, toPlanet))

        when:
        InOutMove move = org.wow.learning.categorizers.CategorizersPackage.planetMadeNoMove(transition)

        then:
        assert move.in == 0 && move.out == 0, 'request defence!!'
    }

    def 'planet move with no enemies around should be estimated by units movement only'() {
        given:
        def fromPlanet = friend('1')
        def toPlanet = friend('1')
        def neutral = neutral()
        toPlanet.setUnits(11) // according regeneration rate

        fromPlanet.addNeighbours(neutral)
        toPlanet.addNeighbours(neutral)

        def from = new World([fromPlanet, neutral])
        def to = new World([toPlanet, neutral])

        def transition = new PlanetTransition(new PlanetState(from, fromPlanet), new PlanetState(to, toPlanet))

        when:
        InOutMove move = org.wow.learning.categorizers.CategorizersPackage.planetSurroundedByNoEnemies(transition)

        then:
        assert move.in == 0 && move.out == 0
    }

    def 'planet surrounded by no friends could only attack'() {
        given:
        def fromPlanet = friend('1')
        def toPlanet = friend('1')
        def enemy = enemy('2')
        toPlanet.setUnits(5) // planet attack!

        fromPlanet.addNeighbours(enemy)
        toPlanet.addNeighbours(enemy)

        def from = new World([fromPlanet, enemy])
        def to = new World([toPlanet, enemy])

        def transition = new PlanetTransition(new PlanetState(from, fromPlanet), new PlanetState(to, toPlanet))

        when:
        InOutMove move = org.wow.learning.categorizers.CategorizersPackage.planetSurroundedByNoFriends(transition)

        then:
        assert move.in == 0 && move.out > 0
    }

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
