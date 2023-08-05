package arrow.tracing.opentracing

import arrow.tracing.fx.Resource

// TODO: combinators for [io.opentracing.Tracer]

/** this Span combines the capabilities of [arrow.tracing.core.Span], [io.opentracing.Span] */
public interface OTSpan : arrow.tracing.core.Span, io.opentracing.Span {
  override fun continueWithChild(name: String): Resource<OTSpan>
}
