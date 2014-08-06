package org.wow.logger

import com.epam.starwors.galaxy.Planet


/**
 * Please, use this class for messagePack only.
 * Nullable fields are bad style for Kotlin
 */
public data class World(val planets: Collection<Planet>? = null)
