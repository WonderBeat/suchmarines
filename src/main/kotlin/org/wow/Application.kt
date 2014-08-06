package org.wow

import com.epam.starwors.game.SocketGame
import com.epam.starwors.bot.Logic
import org.wow.logger.GameLogger
import org.joda.time.DateTime
import java.io.FileOutputStream
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory


fun main(args : Array<String>) {
    val objectMapper = ObjectMapper(SmileFactory())
    val gameLogger = GameLogger(objectMapper.writer()!!)
    val game = SocketGame("10.20.60.2", 10040, "jat5s65p3lypamzg3ztzjyu2rt4tgqt1", gameLogger)
    print("Running...")
    game.start()

    FileOutputStream("dump/" + DateTime.now()!!.toString("MMddhhmmss") + ".dmp").write(gameLogger.dump())
}

fun Logic.and(other: Logic): Logic =
        Logic { planets-> this.step(planets)?.plus(other.step(planets)!!)?.toArrayList() }
