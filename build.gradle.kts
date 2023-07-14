import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.arrowGradleConfig.formatter)
  alias(libs.plugins.arrowGradleConfig.nexus)
  alias(libs.plugins.arrowGradleConfig.versioning)
  alias(libs.plugins.binaryCompatibilityValidator)
  alias(libs.plugins.detekt)
  alias(libs.plugins.kover)
}

allprojects {
  group = property("projects.group").toString()

  repositories {
    google()
    mavenCentral()
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = Runtime.getRuntime().availableProcessors()

    testLogging {
      showExceptions = true
      showStandardStreams = true
      exceptionFormat = TestExceptionFormat.FULL
      events("passed", "skipped", "failed", "standardOut", "standardError")
    }
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Detekt>().configureEach {
  jvmTarget = "1.8"
  reports {
    html.required.set(true)
    sarif.required.set(true)
    txt.required.set(false)
    xml.required.set(false)
  }
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
  jvmTarget = "1.8"
}

// static analysis tool
detekt {
  parallel = true
  buildUponDefaultConfig = true
  allRules = true
}
