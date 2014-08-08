package org.wow.parser

import org.wow.logger.LogsParser
import spock.lang.Ignore
import spock.lang.Specification

/**
 *
 */
class LogsParserTest extends Specification{
    @Ignore("Test dumps required")
    def 'should return some transitions'() {
        given:
        def logsParser = new LogsParser()

        when:
        def games = logsParser.parse("dump")

        then:
        assert games != null
        assert !games.empty
    }
}
