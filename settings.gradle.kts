rootProject.name = "arrow-tracing"

include("core-entrypoint")

// interpreters
include(
  "interpreters",
  "interpreters:opensensus",
  "interpreters:opentracing",
  "interpreters:opentelemetry",
  "interpreters:jaeger",
  "interpreters:datadog",
  "interpreters:lightstep",
  "interpreters:lightstepGrpc",
  "interpreters:lightstepHttp",
  "interpreters:newrelic",
  "interpreters:honeycomb"
)

include("example")
