kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(project(coreEntrypoint))
      }
    }
    jvmMain {
      dependencies {
        implementation("io.opencensus:opencensus-exporter-trace-ocagent:0.28.3")
      }
    }
    //jsMain {
    // dependencies {
    //implementation(npm("@opencensus/core", "0.1.0", generateExternals = true))
    //implementation(npm("@opencensus/nodejs", "0.1.0", generateExternals = true))
    //implementation(npm("@opencensus/nodejs-base", "0.1.0", generateExternals = true))
    //implementation(npm("@opencensus/exporter-ocagent", "0.1.0", generateExternals = true))
    //}
    //}
  }
}
