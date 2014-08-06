package otg.wow.logger
import com.epam.starwors.galaxy.Planet
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import org.wow.logger.GameLogger
import org.wow.logger.World
import spock.lang.Specification

class GameLoggerTest extends Specification {


    def 'dump and read World with message-pack'() {
        given:
        def objectMapper = new ObjectMapper(new SmileFactory())
        def logger = new GameLogger(objectMapper.writer())

        when:
        logger.step([new Planet("12")])
        def out = logger.dump()
        World[] world = objectMapper.readValue(out, World[].class)

        then:
        assert world != null
        assert world.first().planets.first().id == "12"
    }


}
