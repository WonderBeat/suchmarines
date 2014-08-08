package org.wow.learning.vectorizers.transition

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.wow.learning.vectorizers.planet.NeighborBasedPlanetVectorizer
import org.wow.learning.vectorizers.Transition
import org.wow.learning.vectorizers.planet.PlanetState

public class NeighborBasedVectorizer(val planetVectorizer: Vectorizer<PlanetState, Vector>
                                                = NeighborBasedPlanetVectorizer()): Vectorizer<Transition, List<Vector>> {

    /**
     * Vectorizes all user planets. one by one. with 'planetVectorizer'
     */
    override fun vectorize(input: Transition): List<Vector> =
            input.from.planets!!.filter { it.getOwner() == input.user }.map { planetVectorizer.vectorize(PlanetState(input.from, it)) }

}
