@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

dependencies {
  implementation(libs.kotlin.stdlib)
  api(projects.core)
  api(libs.opentracing.api)
  api(libs.opentracing.util)

  testImplementation(libs.bundles.kotest.jvm)
}
