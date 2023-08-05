package arrow.tracing.opentracing

import arrow.tracing.core.Entrypoint
import arrow.tracing.core.Kernel
import arrow.tracing.fx.Resource

/** refined implementation of [Entrypoint] for opentracing TODO: reevaluate convenience functions */
public interface OTEntrypoint : Entrypoint {
  override fun withRoot(name: String): Resource<OTSpan>

  /**
   * convenience function for [withRoot] public suspend fun <A> withRoot( name: String, trace:
   * suspend ResourceScope.(span: OTSpan) -> A ): A = resourceScope { trace(withRoot(name).bind()) }
   */
  override fun continueWithChild(name: String, kernel: Kernel): Resource<OTSpan?>

  /** convenience function for [continueWithChild] */
  /*public suspend fun <A> continueWithChild(
      name: String,
      kernel: Kernel,
      trace: suspend ResourceScope.(span: OTSpan?) -> A
    ): A = resourceScope { trace(continueWithChild(name, kernel).bind()) }
  */
  override fun continueWithChildOrRoot(name: String, kernel: Kernel): Resource<OTSpan>

  /*public suspend fun <A> continueWithChildOrRoot(
    name: String,
    kernel: Kernel,
    trace: suspend ResourceScope.(span: OTSpan) -> A
  ): A = resourceScope { trace(continueWithChildOrRoot(name, kernel).bind()) }*/
}
