package org.wow.examples

import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression
import org.apache.mahout.classifier.sgd.L1
import org.apache.mahout.math.RandomAccessSparseVector
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder
import spock.lang.Specification

class ClassificationExampleTest extends Specification {

    def 'Classify and determine alien life forms!'() {
        given:
        def dictionary = new org.apache.mahout.vectorizer.encoders.Dictionary()
        def genders = ['male', 'female', 'alien']
        def mood = new ContinuousValueEncoder('mood')
        def weight = new ConstantValueEncoder('weight')
        def algo =  new AdaptiveLogisticRegression(3, 2, new L1())
        def rand = new Random()

        when:
        (1..1000).each {
            def currentGender = genders.get(rand.nextInt(3))
            def alienMultiplier = currentGender == 'alien' ? 1.4 : 1
            def vector = new RandomAccessSparseVector(2)
            mood.addToVector(null as byte[], rand.nextInt(100).doubleValue() * alienMultiplier, vector)
            weight.addToVector(null as byte[], rand.nextInt(50).doubleValue() * alienMultiplier, vector)
            algo.train(dictionary.intern(currentGender), vector)
        }
        algo.close()
        def learner = algo.getBest().getPayload().getLearner()
        def goal = new RandomAccessSparseVector(2)
        mood.addToVector(null as byte[], 110d, goal)
        weight.addToVector(null as byte[], 55, goal)

        def result = learner.classifyFull(goal)
        def resultIndex = result.maxValueIndex()

        then:
        assert result.size() == 3
        assert resultIndex == dictionary.intern('alien')

    }
}
