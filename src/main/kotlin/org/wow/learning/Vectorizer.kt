package org.wow.learning

import org.apache.mahout.math.Vector
import org.wow.evaluation.Transition

public trait Vectorizer <IN, OUT> {
    fun vectorize(input: IN): OUT
}
