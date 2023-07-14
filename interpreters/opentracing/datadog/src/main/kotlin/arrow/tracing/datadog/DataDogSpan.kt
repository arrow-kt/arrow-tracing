@file:Suppress("RedundantSuspendModifier")

package arrow.tracing.datadog

import arrow.tracing.core.BooleanValue
import arrow.tracing.core.CharValue
import arrow.tracing.core.DoubleValue
import arrow.tracing.core.FloatValue
import arrow.tracing.core.IntValue
import arrow.tracing.core.Kernel
import arrow.tracing.core.LongValue
import arrow.tracing.core.ShortValue
import arrow.tracing.core.StringValue
import arrow.tracing.core.TraceValue
import arrow.tracing.core.putErrorFields
import arrow.tracing.fx.ExitCase
import arrow.tracing.fx.Resource
import arrow.tracing.fx.resource
import arrow.tracing.opentracing.OTSpan
import datadog.opentracing.DDTracer
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMapAdapter
import java.net.URI

/** TODO datadog.trace.api.Tracer implementation */

// TODO: Tracer by tracer, datadog.trace.api.Tracer by tracer,
internal fun datadogSpan(tracer: DDTracer, span: io.opentracing.Span, uriPrefix: URI?): OTSpan =
  object : OTSpan, io.opentracing.Span by span {
    override suspend fun put(fields: List<Pair<String, TraceValue>>): Unit =
      fields.forEach { (key, traceValue) ->
        when (traceValue) {
          is StringValue -> span.setTag(key, traceValue.value)
          is BooleanValue -> span.setTag(key, traceValue.value)
          is DoubleValue -> span.setTag(key, traceValue.value)
          is FloatValue -> span.setTag(key, traceValue.value)
          is LongValue -> span.setTag(key, traceValue.value)
          is IntValue -> span.setTag(key, traceValue.value)
          is CharValue -> span.setTag(key, "${traceValue.value}")
          is ShortValue -> span.setTag(key, traceValue.value)
        }
      }

    override suspend fun kernel(): Kernel {
      val map = HashMap<String, String>()
      tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, TextMapAdapter(map))
      return Kernel(map)
    }

    override fun continueWithChild(name: String): Resource<OTSpan> =
      resource {
          val childSpan =
            install({ tracer.buildSpan(name).asChildOf(span).start() }) { childSpan, exitCase ->
              when (exitCase) {
                is ExitCase.Failure -> childSpan.log(exitCase.failure.toString()).finish()
                else -> childSpan.finish()
              }
            }
          datadogSpan(tracer, childSpan, uriPrefix)
        }
        .putErrorFields()

    override fun traceId(): String? = span.context().toTraceId()

    override suspend fun spanId(): String? = span.context().toSpanId()

    override suspend fun traceUriPath(): String? =
      uriPrefix?.let { uri ->
        traceId()?.let { trace ->
          spanId()?.let { span -> uri.resolve("/apm/trace/${trace}?spanID=$span").path }
        }
      }
  }

internal suspend fun root(tracer: Tracer, name: String): io.opentracing.Span =
  tracer.buildSpan(name).start()

/** @throws IllegalArgumentException, NullPointerException when headers are incomplete or invalid */
internal suspend fun fromKernel(
  tracer: Tracer,
  name: String,
  kernel: Kernel
): io.opentracing.Span? =
  tracer.extract(Format.Builtin.HTTP_HEADERS, TextMapAdapter(kernel.headers))?.let { context ->
    tracer.buildSpan(name).asChildOf(context).start()
  }

internal suspend fun fromKernelOrRoot(
  tracer: Tracer,
  name: String,
  kernel: Kernel,
): io.opentracing.Span =
  try {
    fromKernel(tracer, name, kernel) ?: root(tracer, name)
  } catch (_: IllegalArgumentException) {
    root(tracer, name)
  } catch (_: NullPointerException) {
    root(tracer, name)
  }
