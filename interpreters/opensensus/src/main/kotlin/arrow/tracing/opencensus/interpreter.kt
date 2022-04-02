@file:Suppress("RedundantSuspendModifier")

package arrow.tracing.opencensus

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import io.opencensus.exporter.trace.ocagent.OcAgentTraceExporter
import io.opencensus.exporter.trace.ocagent.OcAgentTraceExporterConfiguration
import io.opencensus.trace.Sampler

/** the preferred way to register Exporters */
public fun entrypoint(
  serviceName: String,
  sampler: Sampler,
  config:
    OcAgentTraceExporterConfiguration.Builder.() -> OcAgentTraceExporterConfiguration.Builder =
      {
    this
  }
): Resource<OpenCensusEntrypoint> =
  resource {
    OcAgentTraceExporter.createAndRegister(
      config(OcAgentTraceExporterConfiguration.builder().setServiceName(serviceName)).build()
    )
  }
    .release { OcAgentTraceExporter.unregister() }
    .flatMap { Resource.just(entrypoint(sampler)) }
