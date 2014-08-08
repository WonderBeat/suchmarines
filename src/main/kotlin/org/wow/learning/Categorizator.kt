package org.wow.learning

import org.wow.learning.vectorizers.Vectorizer
import java.io.DataOutput
import org.apache.mahout.math.Vector

public class Categorizator<ITEM>(val categorizer: (a: ITEM) -> Int,
                                val vectorizer: Vectorizer<ITEM, Vector>,
                                val learner: Learner<LearningPair>): Learner<ITEM> {


    override fun learn(data: List<ITEM>): DataOutput {
        val learnData = data.map {
            val category = categorizer(it)
            val vector = vectorizer.vectorize(it)
            LearningPair(category, vector)
        }
        return learner.learn(learnData)
    }

}
