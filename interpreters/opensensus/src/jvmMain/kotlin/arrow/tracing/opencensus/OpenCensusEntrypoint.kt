package arrow.tracing.opencensus

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.releaseCase
import arrow.fx.coroutines.resource
import arrow.tracing.model.Entrypoint
import arrow.tracing.model.Kernel
import io.opencensus.trace.Sampler
import io.opencensus.trace.Tracer
import io.opencensus.trace.Tracing

public interface OpenCensusEntrypoint : Entrypoint {
  override suspend fun withRoot(name: String): Resource<OpenCensusSpan>

  override suspend fun continueWith(name: String, kernel: Kernel): Resource<OpenCensusSpan?>

  override suspend fun continueWithOrElseRoot(name: String, kernel: Kernel): Resource<OpenCensusSpan>
}

internal fun entrypoint(sampler: Sampler): OpenCensusEntrypoint {
  val tracer: Tracer = Tracing.getTracer()
  return object : OpenCensusEntrypoint {
    override suspend fun withRoot(name: String): Resource<OpenCensusSpan> =
      resource { root(tracer, name, sampler) } releaseCase { oc, exitCase -> oc.close(exitCase) }

    override suspend fun continueWith(name: String, kernel: Kernel): Resource<OpenCensusSpan?> =
      resource { fromKernel(tracer, name, kernel) } releaseCase { oc, exitCase -> oc?.close(exitCase) }

    override suspend fun continueWithOrElseRoot(name: String, kernel: Kernel): Resource<OpenCensusSpan> =
      resource {
        fromKernelOrElseRoot(
          tracer,
          name,
          kernel,
          sampler
        )
      } releaseCase { oc, exitCase -> oc.close(exitCase) }
  }
}
