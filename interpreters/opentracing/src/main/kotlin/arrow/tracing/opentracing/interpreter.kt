@file:Suppress("RedundantSuspendModifier")

package arrow.tracing.opentracing

import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer

public suspend fun hasRegisteredTracer(): Boolean = GlobalTracer.isRegistered()

public suspend fun registerTracer(tracer: Tracer): Boolean = GlobalTracer.registerIfAbsent(tracer)

public suspend fun tracerOrNull(): Tracer? =
  if (hasRegisteredTracer()) {
    GlobalTracer.get()
  } else null
