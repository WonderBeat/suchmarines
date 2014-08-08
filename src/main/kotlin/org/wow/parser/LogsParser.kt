package org.wow.parser

import org.springframework.core.io.ClassPathResource
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import org.wow.logger.World

/**
 *
 */
public class LogsParser {

    fun parse(sourceDirectoryName: String): Collection<Array<World>> {
        val sourceDirectory = ClassPathResource(sourceDirectoryName).getFile()!!;
        val objectMapper = ObjectMapper(SmileFactory())
        val files: Array<File> = sourceDirectory.listFiles { it.extension.equals(".dmp") }!!
        return files.map {
            objectMapper.readValue(it.readBytes(), javaClass<Array<World>>())!!
        }
    }

}