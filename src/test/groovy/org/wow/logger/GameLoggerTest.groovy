package org.wow.logger
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import org.wow.http.GameClient
import spock.lang.Specification

class GameLoggerTest extends Specification {


    def 'dump and read World with message-pack'() {
        given:
        def objectMapper = new ObjectMapper(new SmileFactory())
        GameClient httpClientMock = Mock()
        def logger = new GameLogger(objectMapper, "", httpClientMock)

        when:
        logger.step([new Planet("12", "", 12, PlanetType.TYPE_B, [])])
        def out = logger.dump()
        SerializedGameTurn[] world = objectMapper.readValue(out, SerializedGameTurn[].class)

        then:
        assert world != null
        assert world.first().planets.first().id == "12"
    }

    def 'json map test'() {
        given:
        def mapper = new ObjectMapper()
        def response = """{
            "turnNumber": 2,
            "playersActions": {
                "actions": [
                    { "to": 2, "from": 244, "unitCount": 45 }
                ]
            }
        }"""

        when:
        def mapped = mapper.readValue(response, GameTurnResponse)

        then:
        assert mapped.turnNumber == 2
        assert mapped.playersActions.actions.first().to == 2
    }


    def 'dump cross referenced planets'() {
        given:
        def objectMapper = new ObjectMapper(new SmileFactory())
        GameClient httpClientMock = Mock()
        def logger = new GameLogger(objectMapper, "", httpClientMock)
        def planetOne = new Planet("12", "", 12, PlanetType.TYPE_A, [])
        def planetTwo = new Planet("13", "", 12, PlanetType.TYPE_B, [])
        planetOne.addNeighbours(planetTwo)
        planetTwo.addNeighbours(planetOne)

        when:
        logger.step([planetOne, planetTwo])
        def out = logger.dump()
        SerializedGameTurn[] world = objectMapper.readValue(out, SerializedGameTurn[].class)

        then:
        assert world != null
        assert world.first().planets.first().id == "12"
    }


}
