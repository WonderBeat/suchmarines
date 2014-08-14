package org.wow.logger

import com.fasterxml.jackson.databind.ObjectMapper
import com.epam.starwors.galaxy.Planet
import java.io.File

public data class GameTurn(val planets: Collection<Planet>, val moves: List<PlayerMove>)

public data class PlayerMove(val from: Int, val to: Int, unitCount: Int)

fun serializedGameTurnToGameTurn(serialized: SerializedGameTurn): GameTurn {
    val moves = serialized.moves.map { PlayerMove(it.from, it.to, it.unitCount) }
    val withoutNeighborns = GameTurn(serialized.planets.map { Planet(it.id, it.owner, it.units, it.`type`,
            listOf()) }, moves)
    val planetPairs = serialized.planets.map { planet -> Pair(planet, withoutNeighborns.planets.firstOrNull { it.getId() == planet.id }) }
    return GameTurn(planetPairs.map { pair ->
        val serializedPlanet = pair.first
        val original = pair.second!!
        val neighbors = serializedPlanet.neighbours.map { id -> withoutNeighborns.planets.firstOrNull { it.getId() == id } }
        neighbors.forEach { original.addNeighbours(it) }
        original
    }, moves)
}

public class LogsParser(val objectMapper: ObjectMapper) {

    fun parse(file: File): List<GameTurn> =
            objectMapper.readValue(file.readBytes(), javaClass<Array<SerializedGameTurn>>())!!
                    .map { serializedGameTurnToGameTurn(it) }

}
