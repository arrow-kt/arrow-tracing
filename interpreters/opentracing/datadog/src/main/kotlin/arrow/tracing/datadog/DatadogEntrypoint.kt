package arrow.tracing.datadog

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resource
import arrow.tracing.core.Kernel
import arrow.tracing.core.putErrorFields
import arrow.tracing.opentracing.OTEntrypoint
import arrow.tracing.opentracing.OTSpan
import datadog.opentracing.DDTracer
import java.net.URI

internal fun datadogEntrypoint(tracer: DDTracer, uriPrefix: URI?): OTEntrypoint =
  object : OTEntrypoint {
    override fun withRoot(name: String): Resource<OTSpan> =
      resource {
          val rootSpan = install({ root(tracer, name) }) { tracer, _ -> tracer.finish() }
          datadogSpan(tracer, rootSpan, uriPrefix)
        }
        .putErrorFields()

    override fun continueWithChild(name: String, kernel: Kernel): Resource<OTSpan?> =
      resource {
          val childSpan =
            install({ fromKernel(tracer, name, kernel) }) { tracer, _ -> tracer?.finish() }
          childSpan?.let { datadogSpan(tracer, it, uriPrefix) }
        }
        .putErrorFields()

    override fun continueWithChildOrRoot(name: String, kernel: Kernel): Resource<OTSpan> =
      resource {
          val childOrRootSpan =
            install({ fromKernelOrRoot(tracer, name, kernel) }) { tracer, _ -> tracer.finish() }
          datadogSpan(tracer, childOrRootSpan, uriPrefix)
        }
        .putErrorFields()
  }
