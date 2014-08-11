package org.wow.learning.persist

import org.apache.hadoop.io.Writable
import org.springframework.core.io.FileSystemResource
import java.io.DataInputStream
import org.apache.mahout.classifier.AbstractVectorClassifier

public class FileClassifierProvider <T : Writable>(databaseFilename: String, val learnerBuilder: () -> T,
                                                   val classifierExtractor: (T) -> AbstractVectorClassifier):
        ClassifierProvider
    {

    private val database = FileSystemResource(databaseFilename)

    override fun provide(): AbstractVectorClassifier {
        val learner = learnerBuilder()
        learner.readFields(DataInputStream(database.getInputStream()!!))
        return classifierExtractor(learner)
    }

}
