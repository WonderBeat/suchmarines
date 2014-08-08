package org.wow.logger

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import com.fasterxml.jackson.databind.ObjectWriter
import com.epam.starwors.galaxy.PlanetType

data class SerializedPlanet(var id: String = "",
                            var owner: String = "",
                            var units: Int = 0,
                            var `type`: PlanetType = PlanetType.TYPE_A,
                            var neighbours: List<String> = listOf())


public class GameLogger(val serializer: ObjectWriter): Logic {

    var states = listOf<SerializedPlanet>()

    override fun step(world: Collection<Planet>?): MutableCollection<Move>? {
        states = states.plus(world!!.map {
            SerializedPlanet(it.getId()!!,
                    it.getOwner()!!,
                    it.getUnits(),
                    it.getType()!!,
                    it.getNeighbours()!!.map { it.getId()!! })
        })
        return arrayListOf()
    }

    fun dump(): ByteArray = serializer.writeValueAsBytes(states)!!
}
