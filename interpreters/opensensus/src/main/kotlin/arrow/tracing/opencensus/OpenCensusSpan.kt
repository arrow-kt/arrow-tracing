@file:Suppress("RedundantSuspendModifier")

package arrow.tracing.opencensus

import arrow.core.continuations.nullable
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Resource
import arrow.tracing.core.BooleanValue
import arrow.tracing.core.CharValue
import arrow.tracing.core.DoubleValue
import arrow.tracing.core.FloatValue
import arrow.tracing.core.IntValue
import arrow.tracing.core.Kernel
import arrow.tracing.core.LongValue
import arrow.tracing.core.ShortValue
import arrow.tracing.core.Span
import arrow.tracing.core.StringValue
import arrow.tracing.core.TraceValue
import arrow.tracing.core.putErrorFields
import arrow.tracing.core.toTraceValue
import io.opencensus.trace.AttributeValue
import io.opencensus.trace.Sampler
import io.opencensus.trace.Status
import io.opencensus.trace.Tracer
import io.opencensus.trace.Tracing
import io.opencensus.trace.propagation.SpanContextParseException
import io.opencensus.trace.propagation.TextFormat
import io.opencensus.trace.propagation.TextFormat.Getter

public interface OpenCensusSpan : Span {
  public val span: io.opencensus.trace.Span
  override suspend fun traceUriPath(): String? = null
  override suspend fun continueWithChild(name: String): Resource<OpenCensusSpan>
}

internal fun opensensusSpan(tracer: Tracer, span: io.opencensus.trace.Span): OpenCensusSpan =
  object : OpenCensusSpan {
    override val span: io.opencensus.trace.Span
      get() = span

    override suspend fun put(fields: List<Pair<String, TraceValue>>): Unit =
      fields.forEach { (key, traceValue) ->
        when (traceValue) {
          is BooleanValue ->
            span.putAttribute(key, AttributeValue.booleanAttributeValue(traceValue.value))
          is CharValue ->
            span.putAttribute(key, AttributeValue.stringAttributeValue("${traceValue.value}"))
          is DoubleValue ->
            span.putAttribute(key, AttributeValue.doubleAttributeValue(traceValue.value))
          is FloatValue ->
            span.putAttribute(key, AttributeValue.doubleAttributeValue(traceValue.value.toDouble()))
          is IntValue ->
            span.putAttribute(key, AttributeValue.longAttributeValue(traceValue.value.toLong()))
          is LongValue ->
            span.putAttribute(key, AttributeValue.longAttributeValue(traceValue.value))
          is ShortValue ->
            span.putAttribute(key, AttributeValue.longAttributeValue(traceValue.value.toLong()))
          is StringValue ->
            span.putAttribute(key, AttributeValue.stringAttributeValue(traceValue.value))
          else -> Unit
        }
      }

    override suspend fun kernel(): Kernel {
      val headers: MutableMap<String, String> = mutableMapOf()
      Tracing.getPropagationComponent().b3Format.inject(span.context, headers, contextSetter)
      return Kernel(headers.toMap())
    }

    override suspend fun continueWithChild(name: String): Resource<OpenCensusSpan> =
      Resource({ child(name) }) { oc, exitCase -> oc.close(exitCase) }.putErrorFields()

    override fun traceId(): String = span.context.traceId.toLowerBase16()

    override suspend fun spanId(): String = span.context.spanId.toLowerBase16()

    private suspend fun child(childName: String): OpenCensusSpan =
      opensensusSpan(tracer, tracer.spanBuilderWithExplicitParent(childName, span).startSpan())
  }

internal suspend fun OpenCensusSpan.close(exitCase: ExitCase): Unit {
  when (exitCase) {
    ExitCase.Completed -> span.setStatus(Status.OK)
    is ExitCase.Cancelled -> span.setStatus(Status.CANCELLED)
    is ExitCase.Failure -> {
      put(
        "error.message" to exitCase.failure.message.orEmpty().toTraceValue(),
        "error.stacktrace" to exitCase.failure.stackTraceToString().toTraceValue()
      )
      span.setStatus(Status.INTERNAL.withDescription(exitCase.failure.message))
    }
  }
  span.end()
}

internal suspend fun root(tracer: Tracer, name: String, sampler: Sampler): OpenCensusSpan =
  opensensusSpan(tracer, tracer.spanBuilder(name).setSampler(sampler).startSpan())

/**
 * @throws SpanContextParseException or NoSuchElementException, NullPointerException when headers
 * are incomplete or invalid
 */
internal suspend fun fromKernel(tracer: Tracer, name: String, kernel: Kernel): OpenCensusSpan? =
  nullable {
    val ctx = Tracing.getPropagationComponent().b3Format.extract(kernel, contextGetter).bind()
    val span = tracer.spanBuilderWithRemoteParent(name, ctx).startSpan().bind()
    opensensusSpan(tracer, span)
  }

internal suspend fun fromKernelOrElseRoot(
  tracer: Tracer,
  name: String,
  kernel: Kernel,
  sampler: Sampler
): OpenCensusSpan =
  try {
    fromKernel(tracer, name, kernel) ?: root(tracer, name, sampler)
  } catch (_: NullPointerException) {
    root(tracer, name, sampler)
  } catch (_: NoSuchElementException) {
    root(tracer, name, sampler)
  } catch (_: SpanContextParseException) {
    root(tracer, name, sampler)
  }

private val contextSetter: TextFormat.Setter<MutableMap<String, String>>
  get() =
    object : TextFormat.Setter<MutableMap<String, String>>() {
      override fun put(carrier: MutableMap<String, String>, key: String, value: String) {
        carrier.plus(key to value)
      }
    }

private val contextGetter: Getter<Kernel>
  get() =
    object : Getter<Kernel>() {
      override fun get(carrier: Kernel, key: String): String? = carrier.headers[key]
    }
