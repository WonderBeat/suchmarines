package org.wow.logger

import com.epam.starwors.galaxy.PlanetType
import org.wow.logger.SerializedPlanet
import org.wow.logger.SerializedWorld
import org.wow.logger.World
import spock.lang.Specification

class LogParserTest extends Specification {

    def 'check neighborns after deserialization'() {
        given:
        def planetOne = new SerializedPlanet("12", "", 12, PlanetType.TYPE_A, ['13'])
        def planetTwo = new SerializedPlanet("13", "", 12, PlanetType.TYPE_B, ['12'])


        when:
        World world = org.wow.logger.LoggerPackage.serializedWorldToWorld(new SerializedWorld([planetOne, planetTwo]))

        then:
        assert world.planets.first().neighbours.first().id == '13'
    }
}
