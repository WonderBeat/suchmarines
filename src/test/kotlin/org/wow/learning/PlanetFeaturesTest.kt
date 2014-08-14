package org.wow.examples

import org.junit.Test
import com.epam.starwors.galaxy.Planet
import com.epam.starwors.galaxy.PlanetType
import org.wow.logger.GameTurn
import kotlin.test.assertEquals
import java.util.ArrayList

/**
 *
 */
public class PlanetFeaturesTest {

    Test fun shouldCalculateEnemies() {
        val planet1 = Planet("12", "player1", 100, PlanetType.TYPE_A, ArrayList<Planet>())
        val planet2 = Planet("12", "player2", 100, PlanetType.TYPE_D, listOf(planet1))
        planet1.addNeighbours(planet2)
        val world = GameTurn(listOf(planet1, planet2))
        assertEquals(50, planet1.enemiesAround(world))
    }

    Test fun shouldCalculateFriends() {
        val planet1 = Planet("12", "player1", 100, PlanetType.TYPE_A, ArrayList<Planet>())
        val planet2 = Planet("12", "player2", 100, PlanetType.TYPE_D, listOf(planet1))
        planet1.addNeighbours(planet2)
        val world = GameTurn(listOf(planet1, planet2))
        assertEquals(50, planet1.friendsAround(world))
    }

    Test fun shouldCalculatePlanetPower() {
        val planet1 = Planet("12", "player1", 100, PlanetType.TYPE_A, ArrayList<Planet>())
        val world = GameTurn(listOf(planet1))
        assertEquals(100, planet1.planetPower(world))
    }
}
