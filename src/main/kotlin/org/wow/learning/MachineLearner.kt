package org.wow.learning

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.hadoop.io.Writable
import org.apache.mahout.classifier.OnlineLearner

public class MachineLearner<ITEM, LEARNER: OnlineLearner>(val categorizer: (a: ITEM) -> Int,
                                val vectorizer: Vectorizer<ITEM, Vector>,
                                val learner: LEARNER): Learner<ITEM, Unit>
                                where LEARNER: Writable {

    override fun learn(data: ITEM) {
        val category = categorizer(data)
        val vector = vectorizer.vectorize(data)
        learner.train(category, vector)
    }
}
