package org.wow.learning

import org.apache.mahout.math.Vector
import java.io.DataOutput


public trait Learner <T, K> {

    fun learn(data: T): K
}
