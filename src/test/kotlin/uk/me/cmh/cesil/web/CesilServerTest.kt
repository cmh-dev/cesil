package uk.me.cmh.cesil.web

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.Closeable
import java.io.FileWriter

class CesilServerTest {

    lateinit var webClient: WebClient
    lateinit var cesilServer: Http4kServer

    @BeforeEach
    fun setUp() {
        cesilServer = cesilServer().start()
        webClient = WebClient()
        webClient.options.isThrowExceptionOnScriptError = false
    }

    @AfterEach
    fun tearDown() {
        webClient.close()
        cesilServer.stop()
    }

    @Test
    fun `when root page is requested and html page is returned with the correct title is displayed`() {
        val page = webClient.getPage<HtmlPage>("http://localhost:8080/")
        assertEquals("CESIL", page.titleText)
    }

    @Test
    fun `when root page is requested and html page is returned with an empty editor`() {
        val page = webClient.getPage<HtmlPage>("http://localhost:8080/")
        assertEquals("", page.getElementById("editor").textContent)
    }



}
