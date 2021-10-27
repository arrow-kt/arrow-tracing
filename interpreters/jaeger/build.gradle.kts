kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(opentracing))
      }
    }
    jvmMain {
      dependencies {
        implementation("io.jaegertracing:jaeger-client:1.6.0")
      }
    }
  }
}
