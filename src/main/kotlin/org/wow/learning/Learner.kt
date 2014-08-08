package org.wow.learning

import org.apache.mahout.math.Vector
import java.io.DataOutput


public trait Learner <T> {
    fun learn(data: List<T>): DataOutput
}
