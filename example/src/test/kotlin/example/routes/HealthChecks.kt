package example.routes

import example.env.Dependencies
import example.withServer
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class HealthChecks :
  StringSpec({
    "health check" {
      withServer { dep: Dependencies ->
        val responseEmpty = get("") {contentType(ContentType.Application.Json)}
        //val response = get("/cohort/datasources") { contentType(ContentType.Application.Json) }
        //assert(response.status == HttpStatusCode.OK)
        //val body = response.bodyAsText()
        //print(body)
      }
    }
  })
