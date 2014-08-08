package org.wow.learning

import org.apache.mahout.math.Vector
import java.io.DataOutput


public data class LearningPair(val actual: Int, val vector: Vector)

public trait Learner {
    fun learn(data: List<LearningPair>): DataOutput
}
