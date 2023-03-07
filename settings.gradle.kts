enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
  id("com.gradle.enterprise") version "3.11.1"
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("gradle/projects.libs.versions.toml"))
      val kotlinVersion: String? by settings
      kotlinVersion?.let { version("kotlin", it) }
    }
  }

  repositories {
    mavenCentral()
  }
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

rootProject.name = "arrow-tracing"

include("core")

// interpreters
include(
  "interpreters",
  "interpreters:opentracing",
  "interpreters:opentelemetry",
  "interpreters:opentracing:jaeger",
  "interpreters:opentracing:datadog"
)
