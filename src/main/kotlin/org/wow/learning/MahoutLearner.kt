package org.wow.learning

import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression
import java.io.DataOutput
import java.io.DataOutputStream
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import org.apache.mahout.math.Vector

public data class LearningPair(val actual: Int, val vector: Vector)


public class MahoutLearner(private val learner: AdaptiveLogisticRegression): Learner<LearningPair> {

    override fun learn(data: List<LearningPair>): DataOutput {
        data.forEach { learner.train(it.actual, it.vector) }
        val byteOutputStream = ByteOutputStream()
        val outputBuffer = DataOutputStream(byteOutputStream)
        learner.close()
        learner.getBest()!!.getPayload()!!.getLearner()!!.write(outputBuffer)
        outputBuffer.flush()
        return outputBuffer
    }
}
