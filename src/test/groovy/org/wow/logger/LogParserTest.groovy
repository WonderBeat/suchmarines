package org.wow.logger

import com.epam.starwors.galaxy.PlanetType
import org.wow.logger.SerializedPlanet
import org.wow.logger.SerializedGameTurn
import org.wow.logger.GameTurn
import spock.lang.Specification

class LogParserTest extends Specification {

    def 'check neighborns after deserialization'() {
        given:
        def planetOne = new SerializedPlanet("12", "", 12, PlanetType.TYPE_A, ['13'])
        def planetTwo = new SerializedPlanet("13", "", 12, PlanetType.TYPE_B, ['12'])
        def move = new SerializedMove(10, 11, 12)


        when:
        GameTurn gameTurn = org.wow.logger.LoggerPackage.serializedGameTurnToGameTurn(
                new SerializedGameTurn([planetOne, planetTwo], [move]))

        then:
        assert gameTurn.planets.first().neighbours.first().id == '13'
        assert gameTurn.moves.first().from == "10"
    }
}
