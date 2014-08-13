package org.wow.learning

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.mahout.classifier.OnlineLearner
import org.apache.hadoop.io.Writable
import java.io.DataOutput
import java.io.DataInput
import java.io.Closeable

public class MachineLearner<ITEM, LEARNER: OnlineLearner>(val categorizer: (a: ITEM) -> Int,
                                val vectorizer: Vectorizer<ITEM, Vector>,
                                val learner: LEARNER): Learner<ITEM, Unit>
                                where LEARNER: Writable {

    override fun learn(data: ITEM) = learner.train(categorizer(data), vectorizer.vectorize(data))
}
