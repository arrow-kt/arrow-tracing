@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

dependencies {
  implementation(libs.kotlin.stdlibCommon)

  api(projects.interpreters.lightstep)
  implementation(libs.lightstep.grpc)
  // grpc dependency with ktor

  testImplementation(libs.bundles.kotest.jvm)
}
