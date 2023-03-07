package arrow.tracing.core

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.releaseCase

/** A span that can be passed around and can create child spans. */
public interface Span {
  /** puts [fields] into this span. */
  public suspend fun put(fields: List<Pair<String, TraceValue>>): Unit

  /** puts [fields] into this span. */
  public suspend fun put(vararg fields: Pair<String, TraceValue>): Unit = put(fields.toList())

  /**
   * The kernel for this span, which can be sent as headers to remote systems, which can then
   * continue this trace by constructing spans that are children of this one.
   */
  public suspend fun kernel(): Kernel

  /** continues with a child span with the given name. */
  public fun continueWithChild(name: String): Resource<Span>

  /**
   * A unique ID for the trace of this span, if available. Useful to include in error messages for
   * example, to find an associated trace.
   */
  public fun traceId(): String?

  /**
   * A unique ID for this span, if available. This can be useful to include in error messages for
   * example, so you can quickly find the associated trace.
   */
  public suspend fun spanId(): String?

  /**
   * A unique URI for this trace, if available. This can be useful to include in error messages for
   * example, so you can quickly find the associated trace.
   */
  public suspend fun traceUriPath(): String?
}

/**
 * Adds error message and stacktrace, if existent, to fields when an error or cancellation is
 * raised/triggered.
 */
public fun <S : Span?> Resource<S>.putErrorFields(): Resource<S> = releaseCase { s, exit ->
  when (exit) {
    is ExitCase.Failure ->
      s?.put(
        Pair(
          exit.failure.message ?: "Failure has been raised: Exitcase.Failure",
          exit.failure.cause?.stackTraceToString().orEmpty().toTraceValue()
        )
      )
    is ExitCase.Cancelled ->
      s?.put(
        Pair(
          exit.exception.message ?: "Span has been cancelled: Exitcase.Cancelled",
          exit.exception.cause?.stackTraceToString().orEmpty().toTraceValue()
        )
      )
    else -> Unit
  }
}
