package uk.me.cmh.cesil.web

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.*
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLTextAreaElement
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


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
        val editor = page.getElementById("editor") as HtmlTextArea
        assertEquals("", editor.text)
    }

    @Test
    fun `when code is run and html page is returned with the results`() {
        val editorPage = webClient.getPage<HtmlPage>("http://localhost:8080/")
        val editor = editorPage.getElementById("editor") as HtmlTextArea
        editor.text = """PRINT "HELLO WORLD"
                      HALT
                      %
                      *"""
        val resultsPage = (editorPage.getElementById("button-run") as HtmlButton).click<HtmlPage>()
        val resultsTable = resultsPage.getElementById("table-results") as HtmlTable
        val results = resultsTable.rows.map { row -> row.getCell(0).textContent }
        assertEquals("PRINT \"HELLO WORLD\"", results[0].trim())
        assertEquals("HALT", results[1].trim())
        assertEquals("%", results[2].trim())
        assertEquals("*", results[3].trim())
        assertEquals("", results[4].trim())
        assertEquals("RESULTS:", results[5].trim())
        assertEquals("HELLO WORLD", results[6].trim())
    }

}