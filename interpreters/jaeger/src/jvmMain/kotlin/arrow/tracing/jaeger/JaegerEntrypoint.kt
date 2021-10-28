@file:Suppress("RedundantSuspendModifier")

package arrow.tracing.jaeger

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import arrow.tracing.model.Entrypoint
import arrow.tracing.model.putErrorFields
import arrow.tracing.model.Kernel
import arrow.tracing.model.Span
import io.opentracing.Tracer
import java.net.URI

internal fun jaegerEntrypoint(tracer: Tracer, uriPrefix: URI?): Entrypoint =
  object : Entrypoint {
    override fun withRoot(name: String): Resource<Span> =
      resource {
        root(tracer, name)
      }.release { it.finish() }
        .map { jaegerSpan(tracer, it, uriPrefix) }
        .putErrorFields()

    override fun continueWith(name: String, kernel: Kernel): Resource<Span?> =
      resource {
        fromKernel(tracer, name, kernel)
      }.release { it?.finish() }
        .map { span ->
          span?.let { jaegerSpan(tracer, it, uriPrefix) }
        }.putErrorFields()

    override fun continueWithOrRoot(name: String, kernel: Kernel): Resource<Span> =
      resource {
        fromKernelOrRoot(tracer, name, kernel)
      }.release { it.finish() }
        .map { jaegerSpan(tracer, it, uriPrefix) }
        .putErrorFields()
  }
