package org.wow.logger

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import com.epam.starwors.galaxy.Planet
import org.springframework.core.io.FileSystemResource

fun serializedWorldToWorld(serialized: SerializedWorld): World {
    val withoutNeighborns = World(planets = serialized.planets.map { Planet(it.id, it.owner, it.units, it.`type`, listOf()) })
    val planetPairs = serialized.planets.map { planet -> Pair(planet, withoutNeighborns.planets!!.firstOrNull { it.getId() == planet.id }) }
    return World(planetPairs.map { pair ->
        val serializedPlanet = pair.first
        val original = pair.second!!
        val neighbors = serializedPlanet.neighbours.map { id -> withoutNeighborns.planets!!.firstOrNull { it.getId() == id } }
        neighbors.forEach { original.addNeighbours(it) }
        original
    })
}

public class LogsParser(val objectMapper: ObjectMapper) {

    fun parse(sourceDirectoryName: String): Collection<List<World>> {
        val sourceDirectory = File(sourceDirectoryName)
        val files: Array<File> = sourceDirectory.listFiles { it.extension.equals("dmp") }!!
        return files.map {
            objectMapper.readValue(it.readBytes(), javaClass<Array<SerializedWorld>>())!!.map { serializedWorldToWorld(it) }
        }
    }

}
