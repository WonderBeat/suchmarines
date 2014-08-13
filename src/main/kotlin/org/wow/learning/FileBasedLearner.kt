package org.wow.learning

import java.io.File
import org.wow.logger.World
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
                          val vectorizer: (List<World>) -> List<T>,
                          val timeoutSec: Long = 200) {

    private val logger = LoggerFactory.getLogger(javaClass<FileBasedLearner<T>>())!!

    fun <B, K : Learner<T, B>> learn(learner: K): K {
        val doneDefer = Promises.defer<K>()!!.env(env)!!.get()!!
        val completePromise = doneDefer.compose()!!
        val complete = Consumer<K> { doneDefer.accept(it) }
        val deferred = Streams.defer<File>()!!.env(env)!!.dispatcher(Environment.THREAD_POOL)!!.get();
        deferred!!.compose()!!
                .map { file -> parser.parse(file!!) }!!.map { game -> vectorizer(game!!) }!!
                .batch(files.size)!!
                .collect()!!
                .map { it!!.flatMap{ it }.forEach { learner.learn(it) }; learner}!!
                .consume(complete)
        files.forEach{ deferred.accept(it) }
        return completePromise.await(timeoutSec, TimeUnit.SECONDS)!!
    }
}
