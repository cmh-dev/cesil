package uk.me.cmh.cesil.web

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.*
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CesilServerTest {

    private lateinit var webClient: WebClient
    private lateinit var cesilServer: Http4kServer

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
    fun `when emulator page is requested and html page is returned with an empty editor`() {
        val page = webClient.getPage<HtmlPage>("http://localhost:8080/")
        val editor = page.getElementById("text-editor") as HtmlTextArea
        assertEquals("", editor.text)
    }

    @Test
    fun `when the docs page is requested and html page is returned with the documentation`() {
        val page = webClient.getPage<HtmlPage>("http://localhost:8080/docs")
        val pageText = page.asNormalizedText()
        assertTrue(pageText.contains("Program structure"), "Program structure not in page text")
        assertTrue(pageText.contains("Instruction reference"), "Instruction reference not in page text")
    }

    @Test
    fun `when code is run the emulator page is returned with the results and the editor containing the code`() {
        val editorPage = webClient.getPage<HtmlPage>("http://localhost:8080/")
        val editor = editorPage.getElementById("text-editor") as HtmlTextArea
        val code = """PRINT "HELLO WORLD"
                      HALT
                      %
                      *"""
        editor.text = code
        val resultsPage = (editorPage.getElementById("button-run") as HtmlButton).click<HtmlPage>()
        val resultsText = resultsPage.getElementById("text-results") as HtmlTextArea
        val results = resultsText.text.lines()
        assertEquals("PRINT \"HELLO WORLD\"", results[0].trim())
        assertEquals("HALT", results[1].trim())
        assertEquals("%", results[2].trim())
        assertEquals("*", results[3].trim())
        assertEquals("", results[4].trim())
        assertEquals("RESULTS:", results[5].trim())
        assertEquals("HELLO WORLD", results[6].trim())
        val resultsPageEditor = resultsPage.getElementById("text-editor") as HtmlTextArea
        assertEquals(code, resultsPageEditor.text)
    }

    private fun buildProgram(vararg lines : String) : String = lines.joinToString(separator = "\n")

}
