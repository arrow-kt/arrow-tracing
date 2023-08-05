package arrow.tracing.fx

import java.util.concurrent.atomic.AtomicReference

public inline fun <V> AtomicReference<V>.update(function: (V) -> V) {
  while (true) {
    val cur = get()
    val upd = function(cur)
    if (compareAndSet(cur, upd)) return
  }
}

@Suppress("NOTHING_TO_INLINE") public inline fun <A> identity(a: A): A = a

public infix fun <T> T.prependTo(list: Iterable<T>): List<T> = listOf(this) + list
