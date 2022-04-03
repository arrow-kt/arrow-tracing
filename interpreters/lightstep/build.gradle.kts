@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

dependencies {
  implementation(libs.kotlin.stdlibCommon)

  api(projects.interpreters.opentracing)
  api(libs.lightstep.jre)

  testImplementation(libs.bundles.kotest.jvm)
}

