package uk.me.cmh.cesil.web

import com.natpryce.konfig.*
import org.http4k.core.*

import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.template.ThymeleafTemplates
import org.http4k.template.ViewModel

val portKey = Key("port", intType)
val config = EnvironmentVariables() overriding ConfigurationProperties.fromResource("default.properties")


data class MainViewModel(val sourceCode: String = "") : ViewModel {
    override fun template(): String {
        return "templates/main.html"
    }
}

fun cesilServer(): Http4kServer {
    val serverPort = config[portKey]
    return cesilServerHander().asServer(Jetty(serverPort))
}

fun cesilServerHander(): HttpHandler {

    val renderer = ThymeleafTemplates().CachingClasspath()

    return routes(
       "/" bind Method.GET to {
            Response(Status.OK)
                .body(
                    renderer.invoke(
                       MainViewModel()
                    )
                )
                .header("Content-Type", ContentType.TEXT_HTML.toHeaderValue())
        }
    )

}