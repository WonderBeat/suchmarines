package org.wow.learning

import org.wow.learning.vectorizers.Vectorizer
import org.apache.mahout.math.Vector
import org.apache.mahout.classifier.OnlineLearner

public class Categorizator<ITEM, out LEARNER: OnlineLearner>(val categorizer: (a: ITEM) -> Int,
                                val vectorizer: Vectorizer<ITEM, Vector>,
                                val learner: LEARNER): Learner<ITEM, LEARNER> {


    override fun learn(data: List<ITEM>): LEARNER {
        data.forEach { learner.train(categorizer(it), vectorizer.vectorize(it)) }
        return learner
    }

}
