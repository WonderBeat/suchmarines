package org.wow.learning

import org.apache.mahout.math.Vector
import org.wow.evaluation.Transition

public trait Vectorizer {
    fun vectorize(move: Transition): List<Vector>
}
