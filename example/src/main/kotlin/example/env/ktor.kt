package example.env

import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.maxAgeDuration
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.resources.Resources
import kotlin.time.Duration.Companion.days
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

public val kotlinXSerializersModule: SerializersModule =
  SerializersModule {
    // contextual(UserWrapper::class) { UserWrapper.serializer(LoginUser.serializer()) }
    // polymorphic(Any::class) { subclass(LoginUser::class, LoginUser.serializer()) }
  }

public fun Application.configure() {
  install(DefaultHeaders)
  install(Resources) { serializersModule = kotlinXSerializersModule }
  install(ContentNegotiation) {
    json(
      Json {
        serializersModule = kotlinXSerializersModule
        isLenient = true
        ignoreUnknownKeys = true
      }
    )
  }
  install(CORS) {
    allowHeader(HttpHeaders.Authorization)
    allowHeader(HttpHeaders.ContentType)
    allowNonSimpleContentTypes = true
    maxAgeDuration = 3.days
  }
}
