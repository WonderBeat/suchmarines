package org.wow

import com.epam.starwors.game.SocketGame
import com.epam.starwors.bot.Logic


fun main(args : Array<String>) {
    val game = SocketGame("10.20.60.2", 10040, "jat5s65p3lypamzg3ztzjyu2rt4tgqt1", CustomLogic().and(CustomLogic()).and(CustomLogic()))
    game.start()
    print("Running...")

}

fun Logic.and(other: Logic): Logic =
        Logic { planets-> this.step(planets)?.plus(other.step(planets)!!)?.toArrayList() }