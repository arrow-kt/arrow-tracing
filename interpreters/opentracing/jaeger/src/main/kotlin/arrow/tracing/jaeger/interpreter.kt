package arrow.tracing.jaeger

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.closeable
import arrow.fx.coroutines.resource
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
