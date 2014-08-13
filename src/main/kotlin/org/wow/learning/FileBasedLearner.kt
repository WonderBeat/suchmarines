package org.wow.learning

import java.io.File
import org.wow.logger.World
import org.slf4j.LoggerFactory
import reactor.core.composable.spec.Promises
import reactor.core.composable.spec.Streams
import org.wow.logger.LogsParser
import reactor.core.Environment
import reactor.function.Consumer

public class FileBasedLearner<T>(
                          val env: reactor.core.Environment,
                          val files: List<File>,
                          val parser: LogsParser,
                          val vectorizer: (List<World>) -> List<T>) {

    private val logger = LoggerFactory.getLogger(javaClass<FileBasedLearner<T>>())!!

    fun <B, K : Learner<T, B>> learn(data: K): K {
        val doneDefer = Promises.defer<K>()!!.env(env)!!.get()!!
        val completePromise = doneDefer.compose()
        val complete = Consumer<K> { doneDefer.accept(it) }
        val deferred = Streams.defer<File>()!!.env(env)!!.dispatcher(Environment.RING_BUFFER)!!.get();
        deferred!!.compose()!!.map { file -> parser.parse(file!!) }!!.map { game -> vectorizer(game!!) }!!
                .reduce ({ (current) -> current!!.getT1()!!.forEach { current.getT2()!!.learn(it) }; current.getT2() },
                        data)!!
                .consume(complete)
        files.forEach{ deferred.accept(it) }
        return completePromise!!.await()!!
    }
}
