package org.wow.logger

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import com.fasterxml.jackson.databind.ObjectWriter


public class GameLogger(val serializer: ObjectWriter): Logic {

    var states = listOf<World>()

    override fun step(world: Collection<Planet>?): MutableCollection<Move>? {
        states = states.plus(World(world))
        return arrayListOf()
    }

    fun dump(): ByteArray = serializer.writeValueAsBytes(states)!!
}
