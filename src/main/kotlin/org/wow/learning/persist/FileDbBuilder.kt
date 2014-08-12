package org.wow.learning.persist

import org.wow.logger.Parser
import org.wow.logger.World
import org.wow.learning.Learner
import org.apache.hadoop.io.Writable
import org.springframework.core.io.FileSystemResource
import java.io.DataOutputStream
import org.slf4j.LoggerFactory
import java.io.Closeable


public class FileDbBuilder<T, K: Writable>(fileName: String, val parser: Parser,
                          val transformer: (List<World>) -> List<T>,
                          val learner: Learner<T, K>) where K: Closeable {

    private val logger = LoggerFactory.getLogger(javaClass<FileDbBuilder<T, K>>())!!

    private val fileResource = FileSystemResource(fileName)

    fun create() {
        if(fileResource.exists()) {
            logger.info("Database already created. Step skipped")
        } else {
            fileResource.getFile()!!.createNewFile()
            var database = learner.learn(parser.parse().flatMap{ transformer(it) })
            database.close()
            database.write(DataOutputStream(fileResource.getOutputStream()!!))
        }
    }
}
