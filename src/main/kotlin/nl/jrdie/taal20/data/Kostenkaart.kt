package nl.jrdie.taal20.data

typealias Kostenkaart = Map<KostenkaartField, Int>

fun Kostenkaart.getField(field: KostenkaartField): Int {
    return this[field]!!
}
