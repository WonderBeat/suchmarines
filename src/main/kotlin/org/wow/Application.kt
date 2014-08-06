package org.wow

import com.epam.starwors.game.SocketGame
import com.epam.starwors.bot.Logic
import org.wow.logger.GameLogger
import org.msgpack.MessagePack
import org.springframework.core.io.FileSystemResource
import org.joda.time.DateTime


fun main(args : Array<String>) {
    val gameLogger = GameLogger(MessagePack())
    val game = SocketGame("10.20.60.2", 10040, "jat5s65p3lypamzg3ztzjyu2rt4tgqt1", gameLogger)
    game.start()
    FileSystemResource("dump/" + DateTime.now().toString() + ".msp" ).getFile()!!.appendBytes(gameLogger.dump())
    print("Running...")

}

fun Logic.and(other: Logic): Logic =
        Logic { planets-> this.step(planets)?.plus(other.step(planets)!!)?.toArrayList() }
