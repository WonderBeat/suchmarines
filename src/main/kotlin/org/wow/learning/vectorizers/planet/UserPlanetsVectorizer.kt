package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.wow.evaluation.transition.PlayerGameTurn
import com.epam.starwors.galaxy.Planet

public class UserPlanetsVectorizer(val planetVectorizer: Vectorizer<Planet, Vector>): Vectorizer<PlayerGameTurn,
        List<Vector>> {

    /**
     * Vectorizes all user planets. one by one. with 'planetVectorizer'
     */
    override fun vectorize(input: PlayerGameTurn): List<Vector> =
            input.from.planets.filter { it.getOwner() == input.playerName }
                    .map { planetVectorizer.vectorize(it) }

}
