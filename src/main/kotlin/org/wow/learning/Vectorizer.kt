package org.wow.learning

import org.apache.mahout.math.Vector
import javafx.animation.Transition

public trait Vectorizer {
    fun vectorize(move: Transition):Vector
}
