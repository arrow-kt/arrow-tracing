package arrow.tracing.jaeger

import arrow.core.computations.nullable
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import arrow.tracing.model.Entrypoint
import arrow.tracing.model.putErrorFields
import arrow.tracing.model.Kernel
import arrow.tracing.model.Span
import io.jaegertracing.internal.exceptions.UnsupportedFormatException
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMapAdapter
import java.net.URI

internal fun jaegerEntrypoint(tracer: Tracer, uriPrefix: URI?): Entrypoint =
  object : Entrypoint {
    override suspend fun withRoot(name: String): Resource<Span> =
      resource {
        tracer.buildSpan(name).start()
      }.release { it.finish() }
        .map { jaegerSpan(tracer, it, uriPrefix) }
        .putErrorFields()

    override suspend fun continueWith(name: String, kernel: Kernel): Resource<Span?> =
      resource {
        nullable {
          val context = tracer.extract(Format.Builtin.HTTP_HEADERS, TextMapAdapter(kernel.headers)).bind()
          tracer.buildSpan(name).asChildOf(context).start()
        }
      }.release { it?.finish() }
        .map { span ->
          span?.let { jaegerSpan(tracer, it, uriPrefix) }
        }.putErrorFields()


    // TODO: waiting for Resource.flatMap
    override suspend fun continueWithOrElseRoot(name: String, kernel: Kernel): Resource<Span> =
      try {
        continueWith(name, kernel)
        //.flatMap { span -> span?.let{Resource.just(it)} ?: withRoot(name) }
      } catch (_: NullPointerException) {
        withRoot(name)
      } catch (_: UnsupportedFormatException) {
        withRoot(name)
      }
  }
