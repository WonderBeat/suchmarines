package org.wow.learning.predict

import org.wow.logger.World


public trait  Predictor <RESULT, INPUT> {
    fun predict(input: INPUT, world: World): RESULT
}
