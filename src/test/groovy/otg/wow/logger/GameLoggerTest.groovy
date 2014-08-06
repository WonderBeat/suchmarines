package otg.wow.logger

import com.epam.starwors.galaxy.Planet
import org.msgpack.MessagePack
import org.wow.logger.GameLogger
import org.wow.logger.World
import spock.lang.Specification

class GameLoggerTest extends Specification {


    def 'dump and read World with message-pack'() {
        given:
        def messagePack = new MessagePack()
        def logger = new GameLogger(messagePack)

        when:
        logger.step([new Planet("12")])
        def out = logger.dump()
        World world = messagePack.read(out, World)

        then:
        assert out.length > 0
        assert world != null
    }


}
