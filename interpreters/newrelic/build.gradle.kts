@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

dependencies {
  implementation(libs.kotlin.stdlibCommon)

  api(projects.coreEntrypoint)
  api(libs.newrelic.telemetry)
  api(libs.newrelic.telemetry.core)
  api(libs.newrelic.telemetry.okhttp)

  testImplementation(libs.bundles.kotest.jvm)
}
