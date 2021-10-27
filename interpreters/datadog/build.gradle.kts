kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(opentracing))
      }
    }
    jvmMain {
      dependencies {
        implementation("com.datadoghq:dd-trace-api:0.89.0")
        implementation("com.datadoghq:dd-trace-ot:0.89.0")
      }
    }
  }
}
