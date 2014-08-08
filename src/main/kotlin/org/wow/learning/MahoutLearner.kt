package org.wow.learning

import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression
import java.io.DataOutput
import java.io.DataOutputStream
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream

public class MahoutLearner(private val learner: AdaptiveLogisticRegression): Learner {

    override fun learn(data: List<LearningPair>): DataOutput {
        data.forEach { learner.train(it.actual, it.vector) }
        val outputBuffer = DataOutputStream(ByteOutputStream())
        learner.getBest()!!.getPayload()!!.getLearner()!!.write(outputBuffer)
        return outputBuffer
    }
}
