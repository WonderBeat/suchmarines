package org.wow.learning.vectorizers.planet

import org.wow.learning.vectorizers.Vectorizer
import org.wow.learning.vectorizers.Transition
import org.apache.mahout.math.Vector

public class UserPlanetsVectorizer(val planetVectorizer: Vectorizer<PlanetState, Vector>): Vectorizer<Transition, List<Vector>> {

    /**
     * Vectorizes all user planets. one by one. with 'planetVectorizer'
     */
    override fun vectorize(input: Transition): List<Vector> =
            input.from.planets!!.filter { it.getOwner() == input.user }.map { planetVectorizer.vectorize(PlanetState(input.from, it)) }

}
