package arrow.tracing.datadog

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import arrow.tracing.core.Entrypoint
import arrow.tracing.opentracing.registerTracer
import arrow.tracing.opentracing.tracerOrNull
import datadog.opentracing.DDTracer
import datadog.opentracing.DDTracer.builder
import java.net.URI

public fun entrypoint(
  serviceName: String,
  uriPrefix: URI? = null,
  config: DDTracer.DDTracerBuilder.() -> DDTracer = { build() }
): Resource<Entrypoint> =
  resource { config(builder().serviceName(serviceName)).also { registerTracer(it) } }
    .release { it.close() }
    .map { datadogEntrypoint(it, uriPrefix) }

public fun globalEntrypoint(uriPrefix: URI? = null): Resource<Entrypoint?> =
  resource { tracerOrNull() }.release { Unit }.map { t ->
    t?.let { datadogEntrypoint(it, uriPrefix) }
  }
