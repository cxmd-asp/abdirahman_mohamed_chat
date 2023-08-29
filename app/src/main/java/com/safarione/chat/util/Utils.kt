package com.safarione.chat.util

import android.content.Context
import android.widget.Toast
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

/**
 * Returns the value of the passed lambda if this [Int] is -1 or null.
 */
inline fun Int?.notValid(validator: () -> Int): Int {
    return if (this != null && this != -1)
        this
    else
        validator()
}

/**
 * Removes the first element matching the given [predicate].
 * @return the element that was removed or null if no match was found
 */
fun <T> MutableIterable<T>.removeFirst(predicate: (T) -> Boolean): T? {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (predicate(next)) {
            iterator.remove()
            return next
        }
    }

    return null
}

private val emptyPersistentList: PersistentList<Nothing> = persistentListOf()
private val emptyPersistentSet: PersistentSet<Nothing> = persistentSetOf()

fun <T> emptyPersistentList(): PersistentList<T> {
    return emptyPersistentList
}

fun <T> emptyImmutableList(): ImmutableList<T> {
    return emptyPersistentList
}

fun <T> emptyPersistentSet(): PersistentSet<T> {
    return emptyPersistentSet
}

fun <T> emptyImmutableSet(): ImmutableSet<T> {
    return emptyPersistentSet
}

object OrderComparator: Comparator<Int> {
    override fun compare(o1: Int, o2: Int): Int {
        return if (o1 == o2)
            0
        else if (o1 == 0 || o2 == 0)
            -o1.compareTo(o2)
        else if (o1 > 0 != o2 > 0)
            -o1.compareTo(o2)
        else
            o1.compareTo(o2)
    }
}

fun toast(context: Context, text: CharSequence) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

fun toast(context: Context, resId: Int) {
    Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
}
