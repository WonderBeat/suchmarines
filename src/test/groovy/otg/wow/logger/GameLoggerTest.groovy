package otg.wow.logger
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import org.wow.logger.GameLogger
import org.wow.logger.SerializedPlanet
import spock.lang.Specification

class GameLoggerTest extends Specification {


    def 'dump and read World with message-pack'() {
        given:
        def objectMapper = new ObjectMapper(new SmileFactory())
        def logger = new GameLogger(objectMapper.writer())

        when:
        logger.step([new Planet("12", "", 12, PlanetType.TYPE_B, [])])
        def out = logger.dump()
        SerializedPlanet[] world = objectMapper.readValue(out, SerializedPlanet[].class)

        then:
        assert world != null
        assert world.first().id == "12"
    }


    def 'dump cross referenced planets'() {
        given:
        def objectMapper = new ObjectMapper(new SmileFactory())
        def logger = new GameLogger(objectMapper.writer())
        def planetOne = new Planet("12", "", 12, PlanetType.TYPE_A, [])
        def planetTwo = new Planet("13", "", 12, PlanetType.TYPE_B, [])
        planetOne.addNeighbours(planetTwo)
        planetTwo.addNeighbours(planetOne)

        when:
        logger.step([planetOne, planetTwo])
        def out = logger.dump()
        SerializedPlanet[] world = objectMapper.readValue(out, SerializedPlanet[].class)

        then:
        assert world != null
        assert world.first().id == "12"
    }


}
