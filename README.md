[![Kotlin Experimental](https://kotl.in/badges/experimental.svg)](https://kotlinlang.org/docs/components-stability.html)
# Arrow Tracing

Arrow Tracing offers a `Entrypoint` type, that allows us easily to define a root `Span` 
and begin another `Span` on other services.

It provides these integrations with - list is still growing:
- Jaeger with Opentracing [JVM]
- Datadog with Opentracing [JVM]
- Opentracing [JVM]