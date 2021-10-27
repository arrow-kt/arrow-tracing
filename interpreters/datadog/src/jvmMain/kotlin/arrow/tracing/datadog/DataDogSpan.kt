package arrow.tracing.datadog

import arrow.core.Nullable
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.releaseCase
import arrow.fx.coroutines.resource
import arrow.tracing.model.BooleanValue
import arrow.tracing.model.CharValue
import arrow.tracing.model.DoubleValue
import arrow.tracing.model.FloatValue
import arrow.tracing.model.IntValue
import arrow.tracing.model.Kernel
import arrow.tracing.model.LongValue
import arrow.tracing.model.ShortValue
import arrow.tracing.model.Span
import arrow.tracing.model.StringValue
import arrow.tracing.model.TraceValue
import arrow.tracing.model.putErrorFields
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMapAdapter
import java.net.URI

internal fun datadogSpan(tracer: Tracer, span: io.opentracing.Span, uriPrefix: URI?): Span =
  object : Span {
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

    override suspend fun span(name: String): Resource<Span> =
      resource {
        tracer.buildSpan(name).asChildOf(span).start()
      }.releaseCase { child, exitCase ->
        when (exitCase) {
          is ExitCase.Failure -> child.log(exitCase.failure.toString()).finish()
          else -> child.finish()
        }
      }.map { datadogSpan(tracer, it, uriPrefix) }.putErrorFields()

    override fun traceId(): String? =
      span.context().toTraceId()

    override suspend fun spanId(): String? =
      span.context().toSpanId()

    override suspend fun traceUriPath(): String? =
      Nullable.zip(
        uriPrefix,
        traceId(),
        spanId()
      ) { uri, trace, span ->
        uri.resolve("/apm/trace/$trace?spanID=$span").path
      }
  }
