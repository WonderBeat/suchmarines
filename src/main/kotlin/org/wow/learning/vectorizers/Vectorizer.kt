package org.wow.learning.vectorizers

public trait Vectorizer <IN, OUT> {
    fun vectorize(input: IN): OUT
}
