package uk.me.cmh.cesil.cli

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream


class CliAppTest {

    private val standardOut = System.out
    private val outputStreamCaptor = ByteArrayOutputStream()

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(standardOut)
    }

    @Test
    fun `when a valid source code file is executed the expected output is returned`() {
        val testSourceCodeFilePath = CliAppTest::class.java.getResource("/test-source-code.txt").path
        main(arrayOf(testSourceCodeFilePath))
        assertEquals("THE ANSWER IS 42", outputStreamCaptor.toString().trim())
    }

}