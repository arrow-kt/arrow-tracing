package arrow.tracing.datadog

import arrow.core.computations.nullable
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import arrow.tracing.model.Entrypoint
import arrow.tracing.model.Kernel
import arrow.tracing.model.Span
import arrow.tracing.model.putErrorFields
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMapAdapter
import java.net.URI

internal fun datadogEntrypoint(
  tracer: Tracer,
  uriPrefix: URI?
): Entrypoint =
  object : Entrypoint {
    override suspend fun withRoot(name: String): Resource<Span> =
      resource {
        tracer.buildSpan(name).start()
      }.release { it.finish() }
        .map { datadogSpan(tracer, it, uriPrefix) }
        .putErrorFields()

    override suspend fun continueWith(name: String, kernel: Kernel): Resource<Span?> =
      resource {
        nullable {
          val context = tracer.extract(Format.Builtin.HTTP_HEADERS, TextMapAdapter(kernel.headers)).bind()
          tracer.buildSpan(name).asChildOf(context).start()
        }
      }.release { it?.finish() }
        .map { child -> child?.let { datadogSpan(tracer, it, uriPrefix) } }
        .putErrorFields()


    override suspend fun continueWithOrRoot(name: String, kernel: Kernel): Resource<Span> {
      TODO("Not yet implemented")
    }
  }
