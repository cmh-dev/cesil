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
    fun `when a valid source code file is executed the expected output is returned`() =
        assertThatOutputIsExpectedWhenAFilePathIsPassed(
            CliAppTest::class.java.getResource("/test-source-code.txt")!!.path,
            "THE ANSWER IS 42"
        )

    @Test
    fun `when an attempt to execute a missing source code file is undertaken an error is returned`() =
        assertThatOutputIsExpectedWhenAFilePathIsPassed(
            "missing-source-code.txt",
            "*** SOURCE CODE FILE NOT FOUND ***"
        )

    @Test
    fun `when no source code file is passed an error is returned`() {
        main(arrayOf())
        assertThatOutputIsAsExpected("*** MISSING SOURCE CODE FILE ARGUMENT ***")
    }

    private fun assertThatOutputIsExpectedWhenAFilePathIsPassed(sourceCodeFilePath: String, expectedOutput: String) {
        main(arrayOf(sourceCodeFilePath))
        assertThatOutputIsAsExpected(expectedOutput)
    }

    private fun assertThatOutputIsAsExpected(expectedOutput: String) =
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim())

}