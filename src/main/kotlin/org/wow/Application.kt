package org.wow

import com.epam.starwors.game.SocketGame
import com.epam.starwors.galaxy.Move
import com.epam.starwors.galaxy.Planet


fun main(args : Array<String>) {
    val game = SocketGame("10.20.60.2", 10040, "jat5s65p3lypamzg3ztzjyu2rt4tgqt1", { planets -> arrayListOf() })
    game.start()
    print("Running...")

}
