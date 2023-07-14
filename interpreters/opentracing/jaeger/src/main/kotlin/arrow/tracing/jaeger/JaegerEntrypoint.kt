package arrow.tracing.jaeger

import arrow.tracing.core.Kernel
import arrow.tracing.fx.Resource
import arrow.tracing.fx.resource
import arrow.tracing.opentracing.OTEntrypoint
import arrow.tracing.opentracing.OTSpan
import io.opentracing.Tracer
import java.net.URI

internal fun jaegerEntrypoint(tracer: Tracer, uriPrefix: URI?): OTEntrypoint =
  object : OTEntrypoint {
    override fun withRoot(name: String): Resource<OTSpan> = resource {
      val root = install({ root(tracer, name) }, { span, _ -> span.finish() })
      installSpan { jaegerSpan(tracer, root, uriPrefix) }
    }

    override fun continueWithChild(name: String, kernel: Kernel): Resource<OTSpan?> = resource {
      install({ fromKernel(tracer, name, kernel) }, { span, _ -> span?.finish() })?.let { span ->
        installSpan { jaegerSpan(tracer, span, uriPrefix) }
      }
    }

    override fun continueWithChildOrRoot(name: String, kernel: Kernel): Resource<OTSpan> =
      resource {
        val span = install({ fromKernelOrRoot(tracer, name, kernel) }, { span, _ -> span.finish() })
        installSpan { jaegerSpan(tracer, span, uriPrefix) }
      }
  }
