package org.wow.learning.vectorizers.transition

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.wow.learning.vectorizers.planet.PlanetTransition
import org.wow.learning.vectorizers.planet.NeighborBasedPlanetVectorizer
import org.wow.learning.vectorizers.Transition

public class NeighborBasedVectorizer(val planetVectorizer: Vectorizer<PlanetTransition, Vector>
                                                = NeighborBasedPlanetVectorizer()): Vectorizer<Transition, List<Vector>> {

    override fun vectorize(input: Transition): List<Vector> =
            input.from.planets!!.filter { it.getOwner() == input.user }.map { planetVectorizer.vectorize(PlanetTransition(input, it)) }

}
