package uk.me.cmh.cesil.web

import com.natpryce.konfig.*
import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.core.body.formAsMap
import org.http4k.routing.ResourceLoader.Companion.Classpath

import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.template.ThymeleafTemplates
import org.http4k.template.ViewModel
import uk.me.cmh.cesil.interpreter.Interpreter

val portKey = Key("port", intType)
val config = EnvironmentVariables() overriding ConfigurationProperties.fromResource("default.properties")

data class MainViewModel(val sourceCode: String = "") : ViewModel {
    override fun template(): String {
        return "templates/main.html"
    }
}

data class RunResultsViewModel(val results: List<String>) : ViewModel {
    override fun template(): String {
        return "templates/run-results.html"
    }
}

fun cesilServer(): Http4kServer {
    val serverPort = config[portKey]
    return cesilServerHander().asServer(Jetty(serverPort))
}

fun cesilServerHander(): HttpHandler {

    val renderer = ThymeleafTemplates().CachingClasspath()

    return routes(
        "/static" bind static(Classpath("/static")),
        "/" bind Method.GET to {
            Response(Status.OK)
                .body(
                    renderer.invoke(
                       MainViewModel()
                    )
                )
                .header("Content-Type", ContentType.TEXT_HTML.toHeaderValue())
        },
        "/run-program" bind Method.POST to { request ->
            val interpreter = Interpreter()
            val parameters: Map<String, List<String?>> = request.formAsMap()
            val sourceCode = parameters.get("sourcecode")?.get(0) ?: ""
            val executionResult = interpreter.executeProgram(sourceCode)
            val results = sourceCode.lines() + listOf("", "RESULTS:") + executionResult.output
            Response(Status.OK)
                .body(
                    renderer.invoke(
                        RunResultsViewModel(results.map {
                            when {
                                it.isBlank() -> " "
                                else -> it
                            }
                        })
                    )
                )
                .header("Content-Type", ContentType.TEXT_HTML.toHeaderValue())
        }
    )

}

fun main() {
    cesilServer().start()
}