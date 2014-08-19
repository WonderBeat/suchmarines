package org.wow.logger
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import org.wow.http.GameClient
import spock.lang.Specification

class GameLoggerTest extends Specification {


    def 'dump and read'() {
        given:
        def objectMapper = new ObjectMapper(new SmileFactory())
        GameClient httpClientMock = Mock()
        httpClientMock.getMovesForPreviousTurn(_) >> []
        def logger = new GameLogger(objectMapper, "", httpClientMock)

        when:
        logger.step([new Planet("12", "", 12, PlanetType.TYPE_B, [])])
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
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        def response = """{"playersActions":{"planetOwners":[{"id":2608,"owner":"bot","unitsCount":59},
                            {"id":2606,"owner":"bot","unitsCount":157},{"id":2607,"owner":"suchbotwow","unitsCount":1000}],
                            "actions":[{"to":2606,"unitCount":37,"from":2608}]},"gameState":"started","turnNumber":23}"""

        when:
        def mapped = mapper.readValue(response, GameTurnResponse)

        then:
        assert mapped.turnNumber == 23
        assert mapped.playersActions.actions.first().to == "2606"
    }


    def 'dump cross referenced planets'() {
        given:
        def objectMapper = new ObjectMapper(new SmileFactory())
        GameClient httpClientMock = Mock()
        httpClientMock.getMovesForPreviousTurn(_) >> []
        def logger = new GameLogger(objectMapper, "", httpClientMock)
        def planetOne = new Planet("12", "", 12, PlanetType.TYPE_A, [])
        def planetTwo = new Planet("13", "", 12, PlanetType.TYPE_B, [])
        planetOne.addNeighbours(planetTwo)
        planetTwo.addNeighbours(planetOne)

        when:
        logger.step([planetOne, planetTwo])
        logger.step([planetOne, planetTwo])
        def out = logger.dump()
        SerializedGameTurn[] world = objectMapper.readValue(out, SerializedGameTurn[].class)

        then:
        assert world != null
        assert world.first().planets.first().id == "12"
    }


}
