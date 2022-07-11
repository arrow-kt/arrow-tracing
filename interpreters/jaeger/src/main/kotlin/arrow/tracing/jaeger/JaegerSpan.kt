package arrow.tracing.jaeger

import arrow.core.Nullable
import arrow.core.continuations.nullable
import arrow.fx.coroutines.Resource
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
import io.jaegertracing.internal.exceptions.UnsupportedFormatException
import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMapAdapter
import java.net.URI

internal fun jaegerSpan(tracer: Tracer, span: Span, prefix: URI?): arrow.tracing.core.Span =
  object : arrow.tracing.core.Span {
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

    override suspend fun continueWithChild(name: String): Resource<arrow.tracing.core.Span> =
      Resource({ tracer.buildSpan(name).asChildOf(span).start() }) { span, _ -> span.finish() }
        .map { jaegerSpan(tracer, it, prefix) }
        .putErrorFields()

    override fun traceId(): String? = span.context().toTraceId()

    override suspend fun spanId(): String? = span.context().toSpanId()

    override suspend fun traceUriPath(): String? =
      Nullable.zip(prefix, traceId()) { uri, id -> uri.resolve("/trace/$id").path }
  }

internal suspend fun root(tracer: Tracer, name: String): io.opentracing.Span =
  tracer.buildSpan(name).start()

/**
 * @throws UnsupportedFormatException or NullPointerException when headers are incomplete or invalid
 */
internal suspend fun fromKernel(
  tracer: Tracer,
  name: String,
  kernel: Kernel
): io.opentracing.Span? = nullable {
  val context = tracer.extract(Format.Builtin.HTTP_HEADERS, TextMapAdapter(kernel.headers)).bind()
  tracer.buildSpan(name).asChildOf(context).start()
}

internal suspend fun fromKernelOrRoot(
  tracer: Tracer,
  name: String,
  kernel: Kernel,
): io.opentracing.Span =
  try {
    fromKernel(tracer, name, kernel) ?: root(tracer, name)
  } catch (_: NullPointerException) {
    root(tracer, name)
  } catch (_: UnsupportedFormatException) {
    root(tracer, name)
  }
