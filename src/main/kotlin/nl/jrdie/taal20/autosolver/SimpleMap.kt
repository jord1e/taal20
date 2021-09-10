package nl.jrdie.taal20.autosolver

import java.util.function.Function

data class SimpleMap<T, U>(override val key: T, override val value: U) :
    Map.Entry<T, U> by java.util.AbstractMap.SimpleImmutableEntry(key, value) {

    companion object {
        fun <K, V> hold(function: Function<K, V>): (K) -> SimpleMap<K, V> {
            return {t: K -> SimpleMap(t, function.apply(t)) }
        }
    }
}