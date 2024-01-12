package example.routes

import example.env.Dependencies
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing

public fun Application.routes(deps: Dependencies): Routing = routing {}

@Resource("/api") public data object RootResource
