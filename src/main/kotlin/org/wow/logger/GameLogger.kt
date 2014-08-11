package org.wow.logger

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import com.fasterxml.jackson.databind.ObjectWriter
import com.epam.starwors.galaxy.PlanetType


data class SerializedWorld(val planets: List<SerializedPlanet> = listOf())

data class SerializedPlanet(val id: String = "",
                            val owner: String = "",
                            val units: Int = 0,
                            val `type`: PlanetType = PlanetType.TYPE_A,
                            val neighbours: List<String> = listOf())


public class GameLogger(val serializer: ObjectWriter): Logic {

    var states: List<SerializedWorld> = arrayListOf()

    override fun step(world: Collection<Planet>?): MutableCollection<Move>? {
        val serializedWorld = SerializedWorld(world!!.map {
            SerializedPlanet(it.getId()!!,
                    it.getOwner()!!,
                    it.getUnits(),
                    it.getType()!!,
                    it.getNeighbours()!!.map { it.getId()!! })
        })
        states = states.plus(serializedWorld)
        return arrayListOf()
    }

    fun dump(): ByteArray = serializer.writeValueAsBytes(states)!!
}
