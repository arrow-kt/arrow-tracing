package arrow.tracing.jaeger

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import arrow.tracing.model.Entrypoint
import arrow.tracing.opentracing.registerTracer
import arrow.tracing.opentracing.tracerOrNull
import io.jaegertracing.Configuration
import io.jaegertracing.internal.JaegerTracer
import java.net.URI

public fun entrypoint(
  serviceName: String,
  uiPrefix: URI? = null,
  config: Configuration.() -> JaegerTracer
): Resource<Entrypoint> =
  resource {
    Configuration(serviceName).config().also { registerTracer(it) }
  }.release { it.close() }
    .map { jaegerEntrypoint(it, uiPrefix) }


public fun globalEntrypoint(uiPrefix: URI? = null): Resource<Entrypoint?> =
  resource { tracerOrNull()?.let { jaegerEntrypoint(it, uiPrefix) } }
    .release { Unit }
