package org.wow.learning.persist

import org.apache.hadoop.io.Writable
import org.springframework.core.io.FileSystemResource
import java.io.DataInputStream
import org.apache.mahout.classifier.AbstractVectorClassifier
import org.slf4j.LoggerFactory

public class FileClassifierProvider <T : Writable>(databaseFilename: String)
    {

    private val logger = LoggerFactory.getLogger(javaClass<FileClassifierProvider<T>>())!!

    private val database = FileSystemResource(databaseFilename)

    fun provide(learner: T): T {
        learner.readFields(DataInputStream(database.getInputStream()!!))
        return learner
    }

}
