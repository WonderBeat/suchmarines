package org.wow.logger

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import org.springframework.core.io.ClassPathResource
import java.io.File
import com.epam.starwors.galaxy.Planet

fun serializedWorldToWorld(serialized: SerializedWorld): World {
    val withoutNeighborns = World(planets = serialized.planets.map { Planet(it.id, it.owner, it.units, it.`type`, listOf()) })
    val planetPairs = serialized.planets.map { planet -> Pair(planet, withoutNeighborns.planets!!.firstOrNull { it.getId() == planet.id }) }
    return World(planetPairs.map { pair ->
        val serialized = pair.first
        val original = pair.second!!
        val neibhorns = serialized.neighbours.map { id -> withoutNeighborns.planets!!.firstOrNull { it.getId() == id } }
        neibhorns.forEach { original.addNeighbours(it) }
        original
    })
}

public class LogsParser {

    fun parse(sourceDirectoryName: String): Collection<List<World>> {
        val sourceDirectory = ClassPathResource(sourceDirectoryName).getFile()!!;
        val objectMapper = ObjectMapper(SmileFactory())
        val files: Array<File> = sourceDirectory.listFiles { it.extension.equals(".dmp") }!!
        return files.map {
            objectMapper.readValue(it.readBytes(), javaClass<Array<SerializedWorld>>())!!.map { serializedWorldToWorld(it) }
        }
    }

}
