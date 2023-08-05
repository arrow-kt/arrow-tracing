package arrow.tracing.datadog

import arrow.tracing.core.Kernel
import arrow.tracing.fx.Resource
import arrow.tracing.fx.resource
import arrow.tracing.opentracing.OTEntrypoint
import arrow.tracing.opentracing.OTSpan
import datadog.opentracing.DDTracer
import java.net.URI

internal fun datadogEntrypoint(tracer: DDTracer, uriPrefix: URI?): OTEntrypoint =
  object : OTEntrypoint {
    override fun withRoot(name: String): Resource<OTSpan> = resource {
      val rootSpan = install({ root(tracer, name) }) { tracer, _ -> tracer.finish() }
      installSpan { datadogSpan(tracer, rootSpan, uriPrefix) }
    }

    override fun continueWithChild(name: String, kernel: Kernel): Resource<OTSpan?> = resource {
      val childSpan =
        install({ fromKernel(tracer, name, kernel) }) { tracer, _ -> tracer?.finish() }
      installSpan { childSpan?.let { datadogSpan(tracer, it, uriPrefix) } }
    }

    override fun continueWithChildOrRoot(name: String, kernel: Kernel): Resource<OTSpan> =
      resource {
        val childOrRootSpan =
          install({ fromKernelOrRoot(tracer, name, kernel) }) { tracer, _ -> tracer.finish() }
        installSpan { datadogSpan(tracer, childOrRootSpan, uriPrefix) }
      }
  }
