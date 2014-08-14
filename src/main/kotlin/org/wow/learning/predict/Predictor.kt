package org.wow.learning.predict

import com.epam.starwors.galaxy.Planet


public trait  Predictor <RESULT, INPUT> {
    fun predict(input: INPUT, world: Collection<Planet>): RESULT
}
