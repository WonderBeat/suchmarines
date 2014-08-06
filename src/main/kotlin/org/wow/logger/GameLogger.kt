package org.wow.logger

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import org.msgpack.MessagePack


public class GameLogger(val msgPack: MessagePack): Logic {

    var states = listOf<World>()

    override fun step(world: Collection<Planet>?): MutableCollection<Move>? {
        states = states.plus(World())
        return arrayListOf()
    }

    fun dump(): ByteArray {
        return msgPack.write(states)!!
    }
}
