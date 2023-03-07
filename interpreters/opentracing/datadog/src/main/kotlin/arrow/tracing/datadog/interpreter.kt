package arrow.tracing.datadog

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.closeable
import arrow.fx.coroutines.resource
import arrow.tracing.opentracing.OTEntrypoint
import arrow.tracing.opentracing.globalTracerOrNull
import datadog.opentracing.DDTracer
import datadog.opentracing.DDTracer.builder
import io.opentracing.util.GlobalTracer
import java.net.URI

/**
 * registers a [DDTracer] as the [GlobalTracer] in a resource-safe manner and returns an
 * [OTEntrypoint]
 */
public fun entrypoint(
  serviceName: String,
  uriPrefix: URI? = null,
  config: DDTracer.DDTracerBuilder.() -> DDTracer = { build() }
): Resource<OTEntrypoint> = resource {
  val ddTracer = closeable {
    config(builder().serviceName(serviceName)).also { GlobalTracer.registerIfAbsent(it) }
  }
  datadogEntrypoint(ddTracer, uriPrefix)
}

/** returns an [OTEntrypoint], based on the current [GlobalTracer], if one exists */
public fun globalEntrypoint(uriPrefix: URI? = null): Resource<OTEntrypoint?> = resource {
  val tracer = install({ globalTracerOrNull() }, { _, _ -> Unit })
  (tracer as? DDTracer)?.let { datadogEntrypoint(it, uriPrefix) }
}
