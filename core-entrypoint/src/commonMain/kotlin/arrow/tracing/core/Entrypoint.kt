package arrow.tracing.core

import arrow.fx.coroutines.Resource

/**
 * An entry point, for creating root spans or continuing traces that were started on another
 * system.
 */
public interface Entrypoint {

  /** Resource that creates a new root span in a new trace.*/
  public fun withRoot(name: String): Resource<Span>

  /**
   * Resource that creates a new span as the child of the span specified by the given kernel,
   * which typically arrives via request headers. By this mechanism we can continue a trace that
   * began in another system. If the required headers are not present in `kernel` an exception will
   * be raised.
   * incomplete or invalid headers in [kernel] cause the resulting Span to be null
   */
  public fun continueWith(name: String, kernel: Kernel): Resource<Span?>

  /**
   * Resource that attempts to create a new span as with `continueWith`, but falls back to a new root
   * span as with `withRoot` if the kernel does not contain the required headers. In other words, we
   * continue the existing span if we can, otherwise we start a new one.
   */
  public fun continueWithOrRoot(name: String, kernel: Kernel): Resource<Span>
}
