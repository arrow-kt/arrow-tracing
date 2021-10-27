package arrow.tracing.model

/**
 * TODO: Can this use EitherEffect
 */
/** A tracing effect, which always has a current span. */
public interface Trace {

  /** Put a sequence of fields into the current span. */
  public suspend fun put(fields: List<Pair<String, TraceValue>>): Unit

  /** Put a sequence of fields into the current span. */
  public suspend fun put(vararg fields: Pair<String, TraceValue>): Unit =
    put(fields.toList())

  /**
   * The kernel for the current span, which can be sent as headers to remote systems, which can
   * then continue this trace by constructing spans that are children of the current one.
   */
  public suspend fun kernel(): Kernel

  /** Create a new span, and within it run the continuation `cont`. */
  public suspend fun <A> span(name: String, cont: suspend () -> A): A

  /**
   * A unique ID for this trace, if available. This can be useful to include in error messages for
   * example, so you can quickly find the associated trace.
   */
  public suspend fun traceId(): String?

  /**
   * A unique URI for this trace, if available. This can be useful to include in error messages for
   * example, so you can quickly find the associated trace.
   */
  public suspend fun traceUriString(): String?
}

public object NoopTrace : Trace {
  override suspend fun put(fields: List<Pair<String, TraceValue>>): Unit = Unit

  override suspend fun kernel(): Kernel = Kernel(emptyMap())
  override suspend fun traceId(): String? = null
  override suspend fun traceUriString(): String? = null

  override suspend fun <A> span(name: String, cont: suspend () -> A): A =
    cont()
}
