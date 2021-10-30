package arrow.tracing.jaeger

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import arrow.tracing.core.Entrypoint
import arrow.tracing.opentracing.registerTracer
import arrow.tracing.opentracing.tracerOrNull
import io.jaegertracing.Configuration
import io.jaegertracing.internal.JaegerTracer
import java.net.URI

public fun entrypoint(
  serviceName: String,
  uriPrefix: URI? = null,
  config: Configuration.() -> JaegerTracer = { tracer }
): Resource<Entrypoint> =
  resource {
    Configuration(serviceName).config().also { registerTracer(it) }
  }.release { it.close() }
    .map { jaegerEntrypoint(it, uriPrefix) }

public fun globalEntrypoint(uriPrefix: URI? = null): Resource<Entrypoint?> =
  resource { tracerOrNull() }
    .release { it?.close() }
    .map { t -> t?.let { jaegerEntrypoint(it, uriPrefix) } }
