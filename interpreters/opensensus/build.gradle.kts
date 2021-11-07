kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(coreEntrypoint))
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.opencensus.exporterTraceOcagent)
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
