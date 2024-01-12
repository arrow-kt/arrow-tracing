import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  application
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.power.assert)
  alias(libs.plugins.ktor)
}

repositories {
  mavenCentral()
}

application {
  mainClass.set("example.MainKt")
}

tasks.withType<JavaCompile>().configureEach {
  sourceCompatibility = "${JavaVersion.VERSION_17}"
  targetCompatibility = "${JavaVersion.VERSION_17}"
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "${JavaVersion.VERSION_17}"
    // adding context receivers
    freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
  }
}

tasks.test {
  useJUnitPlatform()
}

ktor {
  // docker configuration
  docker {
    jreVersion = JavaVersion.VERSION_17
    localImageName = "tracing-example"
    imageTag = "latest"
  }
}

sqldelight {
  databases {
    create("SqlDelight") {
      packageName = "example.sqldelight"
      dialect(libs.sqldelight.postgresql.get())
    }
  }
}


dependencies {
  implementation(libs.coroutines.core)

  // Jaeger for distributed tracing
  implementation(projects.interpreters.opentracing.jaeger)

  testImplementation(libs.kotest.arrow)
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.runnerJUnit5)

  implementation(libs.arrow.fx)
  implementation(libs.bundles.ktor.server)
  implementation(libs.bundles.suspendapp)
  implementation(libs.logback.classic)
  implementation(libs.bundles.cohort)
  implementation(libs.sqldelight.jdbc)
  implementation(libs.hikari)
  implementation(libs.postgresql)
  implementation(libs.sqldelight.postgresql)

  testImplementation(libs.bundles.ktor.client)
  testImplementation(libs.testcontainers.postgresql)
  testImplementation(libs.ktor.server.tests)
  testImplementation(libs.bundles.kotest.example)
}
