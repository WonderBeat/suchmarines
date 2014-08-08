package org.wow

import com.epam.starwors.game.SocketGame
import com.epam.starwors.bot.Logic
import org.wow.logger.GameLogger
import org.joda.time.DateTime
import java.io.FileOutputStream
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory

import org.springframework.core.io.FileSystemResource
import java.io.File
import org.wow.java.logics.UniformAttack


fun main(args : Array<String>) {
    val objectMapper = ObjectMapper(SmileFactory())
    val gameLogger = GameLogger(objectMapper.writer()!!)

    val game = SocketGame("176.192.95.4", 10040, "wpm5dqloq5s6kzxem4j5ixaw4tlu6dee", gameLogger.and(UniformAttack("WooDmaN")))
    print("Running....")
    game.start()

    val file = File("dump/" + DateTime.now()!!.toString("MMddhhmmss") + ".dmp")
    file.getParentFile()!!.mkdirs();
    file.createNewFile();
    FileOutputStream(file).write(gameLogger.dump());
    //FileSystemResource("dump/" + DateTime.now()!!.toString("MMddhhmmss") + ".dmp").getOutputStream()!!.write(gameLogger.dump())
}

fun Logic.and(other: Logic): Logic =
        Logic { planets-> this.step(planets)?.plus(other.step(planets)!!)?.toArrayList() }
