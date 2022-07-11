package arrow.tracing.jaeger

import arrow.fx.coroutines.Resource
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
  Resource({ Configuration(serviceName).config().also { registerTracer(it) } }) { tracer, _ ->
    tracer.close()
  }
    .map { jaegerEntrypoint(it, uriPrefix) }

public fun globalEntrypoint(uriPrefix: URI? = null): Resource<Entrypoint?> =
  Resource({ tracerOrNull() }) { tracer, _ -> tracer?.close() }.map { t ->
    t?.let { jaegerEntrypoint(it, uriPrefix) }
  }
