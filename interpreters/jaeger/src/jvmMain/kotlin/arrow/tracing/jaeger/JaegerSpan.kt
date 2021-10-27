package arrow.tracing.jaeger

import arrow.core.Nullable
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import arrow.tracing.model.BooleanValue
import arrow.tracing.model.CharValue
import arrow.tracing.model.DoubleValue
import arrow.tracing.model.FloatValue
import arrow.tracing.model.IntValue
import arrow.tracing.model.Kernel
import arrow.tracing.model.LongValue
import arrow.tracing.model.ShortValue
import arrow.tracing.model.StringValue
import arrow.tracing.model.TraceValue
import io.opentracing.Span
import io.opentracing.Tracer
import arrow.tracing.model.putErrorFields
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMapAdapter
import java.net.URI

internal fun jaegerSpan(
  tracer: Tracer,
  span: Span,
  prefix: URI?
): arrow.tracing.model.Span =
  object : arrow.tracing.model.Span {
    override suspend fun put(fields: List<Pair<String, TraceValue>>): Unit =
      fields.forEach { (key, traceValue) ->
        when (traceValue) {
          is BooleanValue -> span.setTag(key, traceValue.value)
          is CharValue -> span.setTag(key, "${traceValue.value}")
          is DoubleValue -> span.setTag(key, traceValue.value)
          is FloatValue -> span.setTag(key, traceValue.value)
          is IntValue -> span.setTag(key, traceValue.value)
          is LongValue -> span.setTag(key, traceValue.value)
          is ShortValue -> span.setTag(key, traceValue.value)
          is StringValue -> span.setTag(key, traceValue.value)
          else -> Unit
        }
      }

    override suspend fun kernel(): Kernel {
      val map = HashMap<String, String>()
      tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, TextMapAdapter(map))
      return Kernel(map)
    }

    override suspend fun span(name: String): Resource<arrow.tracing.model.Span> =
      resource { tracer.buildSpan(name).asChildOf(span).start() }
        .release { it.finish() }
        .map { jaegerSpan(tracer, it, prefix) }
        .putErrorFields()

    override fun traceId(): String? =
      span.context().toTraceId()

    override suspend fun spanId(): String? =
      span.context().toSpanId()

    override suspend fun traceUriPath(): String? =
      Nullable.zip(
        prefix,
        traceId()
      ) { uri, id ->
        uri.resolve("/trace/$id").path
      }
  }
