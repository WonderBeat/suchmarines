package org.wow.logger

import com.fasterxml.jackson.databind.ObjectMapper
import com.epam.starwors.galaxy.Planet
import java.io.File

fun serializedWorldToWorld(serialized: SerializedWorld): World {
    val withoutNeighbors = World(planets = serialized.planets.map { Planet(it.id, it.owner, it.units, it.`type`, arrayListOf()) })
    val planetPairs = serialized.planets.map { planet -> Pair(planet, withoutNeighbors.planets!!.firstOrNull { it.getId() == planet.id }) }
    return World(planetPairs.map { pair ->
        val serializedPlanet = pair.first
        val original = pair.second!!
        val neighbors = serializedPlanet.neighbours.map { id -> withoutNeighbors.planets!!.firstOrNull { it.getId() == id } }
        neighbors.forEach { original.addNeighbours(it) }
        original
    })
}

public class LogsParser(val objectMapper: ObjectMapper) {

    fun parse(file: File): List<World> =
            objectMapper.readValue(file.readBytes(), javaClass<Array<SerializedWorld>>())!!
                    .map { serializedWorldToWorld(it) }

}
