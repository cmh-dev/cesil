package uk.me.cmh.cesil.web

import com.natpryce.konfig.*
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import uk.me.cmh.cesil.interpreter.Interpreter

val portKey = Key("port", intType)
val config = EnvironmentVariables() overriding ConfigurationProperties.fromResource("default.properties")

fun main() {
    cesilServer().start()
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
        static("static") {
            resources("static")
        }
        get("/") {
            call.respond(ThymeleafContent(INTERPRETER_MODEL_AND_TEMPLATE, mapOf(INTERPRETER_MODEL_AND_TEMPLATE to InterpreterModel())))
        }
        post("/") {
            val sourceCode = call.receiveParameters()["sourceCode"] ?: ""
            val output = Interpreter().executeProgram(sourceCode).output.joinToString(separator = "\n")
            call.respond(ThymeleafContent(INTERPRETER_MODEL_AND_TEMPLATE, mapOf(INTERPRETER_MODEL_AND_TEMPLATE to InterpreterModel(sourceCode, output))))

        }
        get("/docs") {
            call.respond(ThymeleafContent("docs", mapOf()))
        }
    }

}
