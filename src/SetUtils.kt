fun <E> Sequence<Set<E>>.intersectAll(): Set<E> =
    fold(firstOrNull() ?: emptySet()) { acc, set ->
        acc.intersect(set)
    }

fun <E> Sequence<Set<E>>.mergeAll(): Set<E> =
    fold(mutableSetOf()) { set, values ->
        set.addAll(values); set
    }