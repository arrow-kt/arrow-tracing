package arrow.tracing.core

import arrow.fx.coroutines.Resource

/**
 * [Entrypoint] creates root spans or continues traces with a child span that were started on another system.
 */
public interface Entrypoint {

  /** convenience function for [withRoot] **/
  public suspend fun <A> withRoot(name: String, trace: suspend (Span) -> A): A =
    withRoot(name).use(trace)

  /** creates a new root span in a new trace.*/
  public fun withRoot(name: String): Resource<Span>

  /** convenience function for [continueWithChild] **/
  public suspend fun <A> continueWithChild(name: String, kernel: Kernel, trace: suspend (Span?) -> A): A =
    continueWithChild(name, kernel).use(trace)

  /**
   * continues with a child span specified by the given kernel, this may arrive via request headers.
   * This allows to continue a trace that began in another system.
   * If the required headers are not present in [kernel] an exception will be raised.
   * Incomplete or invalid [Kernel.headers] in [kernel] cause the resulting Span to be null
   */
  public fun continueWithChild(name: String, kernel: Kernel): Resource<Span?>

  /** convenience function for [continueWithChildOrRoot] **/
  public suspend fun <A> continueWithChildOrRoot(name: String, kernel: Kernel, trace: suspend (Span) -> A): A =
    continueWithChildOrRoot(name, kernel).use(trace)

  /**
   * continues with a child span like [continueWithChild], but falls back to a new root if the kernel does not contain the required headers.
   * In other words, [continueWithChildOrRoot] continues the existing span with a child, otherwise starts a new root span.
   */
  public fun continueWithChildOrRoot(name: String, kernel: Kernel): Resource<Span>
}
