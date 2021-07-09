package uk.me.cmh.cesil.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

class AppTest {

    @Test
    fun `running the main program with a CESIL source code file will result it being run with the correct output`() {
        val sourceCodeFilePath = File(this::class.java.getResource("/test-cesil-source-code/main-test-program.txt").toURI()).path
        val out = ByteArrayOutputStream()
        System.setOut(PrintStream(out))
        main(arrayOf(sourceCodeFilePath))
        out.flush()
        val outStr = out.toString()
        assertEquals("THE TOTAL IS: 6", outStr.lines().first())
    }

}

