package org.wow.learning.predict


public trait  Predictor <RESULT, INPUT> {
    fun predict(input: INPUT): RESULT
}
