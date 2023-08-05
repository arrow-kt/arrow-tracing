@file:Suppress("RedundantSuspendModifier")

package arrow.tracing.opentracing

import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer

/** returns the [GlobalTracer] if one is registered or null */
public suspend fun globalTracerOrNull(): Tracer? =
  if (GlobalTracer.isRegistered()) {
    GlobalTracer.get()
  } else null
