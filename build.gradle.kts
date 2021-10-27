import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  kotlin("multiplatform") version "1.5.31" apply false
  id("org.jlleitschuh.gradle.ktlint") version "10.1.0" apply true
  id("io.kotest.multiplatform") version "5.0.0.5"
  id("io.arrow-kt.arrow-gradle-config-nexus") version "0.3.2"
  id("io.arrow-kt.arrow-gradle-config-publish-multiplatform") version "0.3.2"
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
      exclude("build.gradle.kts")
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
            implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
            compileOnly("io.arrow-kt:arrow-fx-coroutines:1.0.1-SNAPSHOT")
          }
        }

        val jvmMain by getting {
          dependsOn(commonMain)
        }

        val commonTest by getting {
          dependsOn(commonMain)
          dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
            implementation("io.kotest:kotest-assertions-core:5.0.0.M3")
            implementation("io.kotest:kotest-property:5.0.0.M3")
            implementation("io.arrow-kt:arrow-fx-coroutines:1.0.1-SNAPSHOT")
          }
        }

        val jvmTest by getting {
          dependsOn(commonTest)
          dependsOn(jvmMain)
          dependencies {
            implementation("io.kotest:kotest-runner-junit5:5.0.0.M3")
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
