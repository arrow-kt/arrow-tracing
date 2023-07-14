package arrow.tracing.jaeger

import arrow.tracing.core.Kernel
import arrow.tracing.core.putErrorFields
import arrow.tracing.fx.Resource
import arrow.tracing.fx.resource
import arrow.tracing.opentracing.OTEntrypoint
import arrow.tracing.opentracing.OTSpan
import io.opentracing.Tracer
import java.net.URI

internal fun jaegerEntrypoint(tracer: Tracer, uriPrefix: URI?): OTEntrypoint =
  object : OTEntrypoint {
    override fun withRoot(name: String): Resource<OTSpan> =
      resource {
          val root = install({ root(tracer, name) }, { span, _ -> span.finish() })
          jaegerSpan(tracer, root, uriPrefix)
        }
        .putErrorFields()

    override fun continueWithChild(name: String, kernel: Kernel): Resource<OTSpan?> =
      resource {
          install({ fromKernel(tracer, name, kernel) }, { span, _ -> span?.finish() })?.let { span
            ->
            jaegerSpan(tracer, span, uriPrefix)
          }
        }
        .putErrorFields()

    override fun continueWithChildOrRoot(name: String, kernel: Kernel): Resource<OTSpan> =
      resource {
          val span =
            install({ fromKernelOrRoot(tracer, name, kernel) }, { span, _ -> span.finish() })
          jaegerSpan(tracer, span, uriPrefix)
        }
        .putErrorFields()
  }
