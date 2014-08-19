package org.wow.logger

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import com.epam.starwors.galaxy.PlanetType
import com.fasterxml.jackson.databind.ObjectMapper
import org.wow.http.GameClient


data class SerializedGameTurn(val planets: List<SerializedPlanet> = arrayListOf(),
                              var moves: List<PlayerMove> = arrayListOf())

data class SerializedPlanet(val id: String = "",
                            val owner: String = "",
                            val units: Int = 0,
                            val `type`: PlanetType = PlanetType.TYPE_A,
                            val neighbours: List<String> = listOf())

data class PlayerActionsResponse(val actions: List<PlayerMove> = listOf())

data class GameTurnResponse(val turnNumber: Int = 0, val playersActions: PlayerActionsResponse = PlayerActionsResponse())


public class GameLogger(val serializer: ObjectMapper,
                        val gameId: String,
                        val client: GameClient): Logic {

    var states: List<GameTurn> = arrayListOf()

    /**
     * Game turn contains world + moves.
     * But moves could be requested only after this game turn
     */
    var lastWorld: Collection<Planet>? = null

    override fun step(world: Collection<Planet>?): MutableCollection<Move>? {
        if(world!!.empty) {     // end of the game
            return arrayListOf()
        }
        if(lastWorld != null) {
            states = states.plus(GameTurn(lastWorld!!, client.getMovesForPreviousTurn(gameId)))
        }
        lastWorld = world
        return arrayListOf()
    }

    private fun serializePlanet(planet: Planet): SerializedPlanet = SerializedPlanet(planet.getId()!!,
            planet.getOwner()!!,
            planet.getUnits(), planet.getType()!!,
            planet.getNeighbours()!!.map { it.getId()!! })

    fun dump(): ByteArray = serializer.writeValueAsBytes(states.
            map { SerializedGameTurn(it.planets.map { serializePlanet(it) }, it.moves) })!!
}
