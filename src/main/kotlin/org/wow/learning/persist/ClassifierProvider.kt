package org.wow.learning.persist

import org.apache.mahout.classifier.AbstractVectorClassifier


public trait ClassifierProvider {
    fun provide(): AbstractVectorClassifier
}
