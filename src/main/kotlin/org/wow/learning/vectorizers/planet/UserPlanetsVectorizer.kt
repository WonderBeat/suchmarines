package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.wow.evaluation.transition.Transition

public class UserPlanetsVectorizer(val planetVectorizer: Vectorizer<PlanetState, Vector>): Vectorizer<Transition, List<Vector>> {

    /**
     * Vectorizes all user planets. one by one. with 'planetVectorizer'
     */
    override fun vectorize(input: Transition): List<Vector> =
            input.sourceWorld.planets!!.filter { it.getOwner() == input.playerName }
                    .map { planetVectorizer.vectorize(PlanetState(input.sourceWorld, it)) }

}
