package org.wow.learning

import java.io.File
import org.wow.logger.GameTurn
import org.slf4j.LoggerFactory
import reactor.core.composable.spec.Promises
import reactor.core.composable.spec.Streams
import org.wow.logger.LogsParser
import reactor.core.Environment
import reactor.function.Consumer
import java.util.concurrent.TimeUnit

public class FileBasedLearner<T>(
                          val env: reactor.core.Environment,
                          val files: List<File>,
                          val parser: LogsParser,
                          val vectorizer: (List<GameTurn>) -> List<T>,
                          val timeoutMin: Long = 1000) {

    private val logger = LoggerFactory.getLogger(javaClass<FileBasedLearner<T>>())!!

    fun <B, K : Learner<T, B>> learn(data: K): K {
        val doneDefer = Promises.defer<K>()!!.env(env)!!.get()!!
        val completePromise = doneDefer.compose()
        val complete = Consumer<K> { doneDefer.accept(it) }
        val deferred = Streams.defer<File>()!!.env(env)!!.dispatcher(Environment.RING_BUFFER)!!.get();
        deferred!!.compose()!!
                .map { file ->
                    logger.debug("Parsing: " + file)
                    parser.parse(file!!) }!!
                .map { game ->
                    vectorizer(game!!)}!!
                .batch(files.size)!!
                .reduce ({ (current) ->
                    current!!.getT1()!!.forEach { current.getT2()!!.learn(it) }; current.getT2()
                    }, data)!!
                .consume(complete)!!
                .`when`(javaClass<Exception>(), { ex ->
                    logger.error(ex.toString())})
        files.forEach{ deferred.accept(it) }
        return completePromise!!.await(timeoutMin, TimeUnit.MINUTES)!!
    }
}
