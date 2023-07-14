package arrow.tracing.jaeger

import arrow.tracing.fx.Resource
import arrow.tracing.fx.closeable
import arrow.tracing.fx.resource
import arrow.tracing.opentracing.OTEntrypoint
import arrow.tracing.opentracing.globalTracerOrNull
import io.jaegertracing.Configuration
import io.jaegertracing.internal.JaegerTracer
import io.opentracing.util.GlobalTracer
import java.net.URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public fun entrypoint(
  serviceName: String,
  uriPrefix: URI? = null,
  config: Configuration.() -> JaegerTracer = { tracer }
): Resource<OTEntrypoint> = resource {
  val jaegerTracer: JaegerTracer = closeable {
    Configuration(serviceName).config().also { GlobalTracer.registerIfAbsent(it) }
  }
  jaegerEntrypoint(jaegerTracer, uriPrefix)
}

public fun globalEntrypoint(uriPrefix: URI? = null): Resource<OTEntrypoint?> = resource {
  val globalTracer =
    install({ globalTracerOrNull() }) { tracer, _ ->
      withContext(Dispatchers.IO) { tracer?.close() }
    }
  globalTracer?.let { jaegerEntrypoint(it, uriPrefix) }
}
