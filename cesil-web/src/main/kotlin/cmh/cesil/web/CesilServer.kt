package cmh.cesil.web

import com.natpryce.konfig.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import cmh.cesil.interpreter.Interpreter

val portKey = Key("port", intType)
val config = EnvironmentVariables() overriding ConfigurationProperties.fromResource("default.properties")

fun main() {
    cesilServer().start(wait = true)
}

private const val INTERPRETER_MODEL_AND_TEMPLATE = "interpreter"

fun cesilServer() = embeddedServer(Netty, port = config[portKey]) {

    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }

    routing {
        staticResources(remotePath = "static", basePackage = "static")
        get("/") {
            call.respond(
                ThymeleafContent(
                    INTERPRETER_MODEL_AND_TEMPLATE,
                    mapOf(INTERPRETER_MODEL_AND_TEMPLATE to InterpreterModel())
                )
            )
        }
        post("/") {
            val sourceCode = call.receiveParameters()["sourceCode"] ?: ""
            val output = Interpreter().executeProgram(sourceCode).output.joinToString(separator = "\n")
            call.respond(
                ThymeleafContent(
                    INTERPRETER_MODEL_AND_TEMPLATE,
                    mapOf(INTERPRETER_MODEL_AND_TEMPLATE to InterpreterModel(sourceCode, output))
                )
            )

        }
        get("/docs") {
            call.respond(ThymeleafContent("docs", mapOf()))
        }
    }

}
