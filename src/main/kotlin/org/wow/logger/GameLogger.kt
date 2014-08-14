package org.wow.logger

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move
import com.epam.starwors.galaxy.PlanetType
import com.fasterxml.jackson.databind.ObjectMapper
import org.wow.http.GameClient


data class SerializedGameTurn(val planets: List<SerializedPlanet> = arrayListOf(),
                              var moves: List<SerializedMove> = arrayListOf())

data class SerializedPlanet(val id: String = "",
                            val owner: String = "",
                            val units: Int = 0,
                            val `type`: PlanetType = PlanetType.TYPE_A,
                            val neighbours: List<String> = listOf())

data class SerializedMove(val from: Int = 0, val to: Int = 9, val unitCount: Int = 9)

data class PlayerActionsResponse(val actions: List<SerializedMove> = listOf())

data class GameTurnResponse(val turnNumber: Int = 0, val playersActions: PlayerActionsResponse = PlayerActionsResponse())


public class GameLogger(val serializer: ObjectMapper,
                        val gameId: String,
                        val client: GameClient): Logic {

    var states: List<SerializedGameTurn> = arrayListOf()

    override fun step(world: Collection<Planet>?): MutableCollection<Move>? {
        if(world!!.empty) {     // end of the game
            return arrayListOf()
        }
        if(states.last != null) {
            states.last!!.moves = client.getMovesForPreviousTurn(gameId)
        }

        val serializedWorld = SerializedGameTurn(world.map {
            SerializedPlanet(it.getId()!!,it.getOwner()!!, it.getUnits(), it.getType()!!,
                    it.getNeighbours()!!.map { it.getId()!! })
        })
        states = states.plus(serializedWorld)
        return arrayListOf()
    }



    fun dump(): ByteArray = serializer.writeValueAsBytes(states)!!
}
