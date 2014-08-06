package org.wow

import com.epam.starwors.game.SocketGame


fun main(args : Array<String>) {
    SocketGame("10.0.2.3", 8080, "token", {planets -> null})
    print("Running...")

}
