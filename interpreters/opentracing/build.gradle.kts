kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(core))
      }
    }
    jvmMain {
      dependencies {
        implementation("io.opentracing:opentracing-api:0.33.0")
        implementation("io.opentracing:opentracing-util:0.33.0")
      }
    }
  }
}
