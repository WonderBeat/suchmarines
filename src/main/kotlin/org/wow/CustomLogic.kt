package org.wow

import com.epam.starwors.bot.Logic
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.Move

/**
 *
 */
public class CustomLogic : Logic {
    override fun step(p0: Collection<Planet>?): MutableCollection<Move> {
        throw UnsupportedOperationException()
    }
}