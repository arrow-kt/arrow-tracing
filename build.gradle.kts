@file:kotlin.Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.ktlint)
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.arrowGradleConfig.nexus)
  alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
}

allprojects {
  apply(plugin = "io.kotest.multiplatform")
  apply(plugin = "org.jlleitschuh.gradle.ktlint")
  apply(plugin = "org.gradle.idea")

  group = "io.arrow-kt"
  version = "0.1.0-SNAPSHOT"

  repositories {
    google()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf("-Xjsr305=strict")
    }
  }

  ktlint {
    filter {
      exclude("build.gradle.kts", "settings.gradle.kts")
    }
  }
}

subprojects {
  if (!listOf("interpreters").contains(name)) {
    apply(plugin = "org.jetbrains.kotlin.multiplatform")

    kotlin {
      explicitApi()

      targets {
        jvm {
          compilations.all {
            kotlinOptions {
              jvmTarget = "1.8"
            }
          }
        }
      }

      sourceSets {
        val commonMain by getting {
          dependencies {
            implementation(rootProject.libs.kotlin.stdlibCommon)
            compileOnly(rootProject.libs.arrow.fx)
          }
        }

        val jvmMain by getting {
          dependsOn(commonMain)
        }

        val commonTest by getting {
          dependsOn(commonMain)
          dependencies {
            implementation(rootProject.libs.kotest.frameworkEngine)
            implementation(rootProject.libs.coroutines.core)
            implementation(rootProject.libs.kotest.assertionsCore)
            implementation(rootProject.libs.kotest.property)
            implementation(rootProject.libs.arrow.fx)
          }
        }

        val jvmTest by getting {
          dependsOn(commonTest)
          dependsOn(jvmMain)
          dependencies {
            implementation(rootProject.libs.kotest.runnerJUnit5)
          }
        }
      }
    }

    tasks.named<Test>("jvmTest") {
      useJUnitPlatform()
      testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
          TestLogEvent.FAILED,
          TestLogEvent.PASSED
        )
        exceptionFormat = TestExceptionFormat.FULL
      }
    }
  }
}

fun Project.kotlin(configure: Action<KotlinMultiplatformExtension>): Unit =
  (this as ExtensionAware).extensions.configure("kotlin", configure)

fun KotlinMultiplatformExtension.targets(configure: Action<Any>): Unit =
  (this as ExtensionAware).extensions.configure("targets", configure)

fun KotlinMultiplatformExtension.sourceSets(configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>): Unit =
  (this as ExtensionAware).extensions.configure("sourceSets", configure)
