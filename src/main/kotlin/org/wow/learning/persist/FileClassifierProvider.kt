package org.wow.learning.persist

import org.apache.hadoop.io.Writable
import org.springframework.core.io.FileSystemResource
import java.io.DataInputStream
import org.apache.mahout.classifier.AbstractVectorClassifier
import org.slf4j.LoggerFactory

public class FileClassifierProvider <T : Writable>(databaseFilename: String, val emptyLearnerBuilder: () -> T,
                                                   val classifierExtractor: (T) -> AbstractVectorClassifier):
        ClassifierProvider
    {

    private val logger = LoggerFactory.getLogger(javaClass<FileClassifierProvider<T>>())!!

    private val database = FileSystemResource(databaseFilename)

    override fun provide(): AbstractVectorClassifier {
        val learner = emptyLearnerBuilder()
        learner.readFields(DataInputStream(database.getInputStream()!!))
        return classifierExtractor(learner)
    }

}
