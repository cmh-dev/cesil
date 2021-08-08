package uk.me.cmh.cesil.web

import com.natpryce.konfig.*
import org.http4k.core.*
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

data class DocsViewModel(val sourceCode: String = "") : ViewModel {
    override fun template(): String {
        return "templates/docs.html"
    }
}

data class EmulatorViewModel(val emulatorModel: EmulatorModel) : ViewModel {
    override fun template(): String {
        return "templates/emulator.html"
    }
}

data class EmulatorModel(val sourceCode: String = "", val output: String = "")

fun cesilServer(): Http4kServer {
    val serverPort = config[portKey]
    return cesilServerHandler().asServer(Jetty(serverPort))
}

fun cesilServerHandler(): HttpHandler {

    val renderer = ThymeleafTemplates().CachingClasspath()

    return routes(
        "/static" bind static(Classpath("/static")),
        "/" bind Method.GET to {
            Response(Status.OK)
                .body(
                    renderer.invoke(
                       EmulatorViewModel(EmulatorModel())
                    )
                )
                .header("Content-Type", ContentType.TEXT_HTML.toHeaderValue())
        },
        "/" bind Method.POST to { request ->
            val interpreter = Interpreter()
            val parameters: Map<String, List<String?>> = request.formAsMap()
            val sourceCode = parameters["sourcecode"]?.get(0) ?: ""
            val output = interpreter.executeProgram(sourceCode).output.joinToString(separator = "\n")
            Response(Status.OK)
                .body(
                    renderer.invoke(
                        EmulatorViewModel(
                            EmulatorModel(
                                sourceCode,
                                output
                            )
                        )
                    )
                )
                .header("Content-Type", ContentType.TEXT_HTML.toHeaderValue())
        },
        "/docs" bind Method.GET to {
            Response(Status.OK)
                .body(
                    renderer.invoke(
                        DocsViewModel()
                    )
                )
                .header("Content-Type", ContentType.TEXT_HTML.toHeaderValue())
        },
    )

}

fun main() {
    cesilServer().start()
}