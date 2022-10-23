package com.wing.tree.bruni.stopwatch.model

import com.wing.tree.bruni.stopwatch.constant.FIVE
import com.wing.tree.bruni.stopwatch.constant.SPACE
import java.text.SimpleDateFormat

data class Lap(
    val lap: Int,
    val lapTime: Long,
    val overallTime: Long,
    val update: Update? = null
) {
    fun toText(simpleDateFormat: SimpleDateFormat): String {
        val lapTime = simpleDateFormat.format(lapTime)
        val overallTime = simpleDateFormat.format(overallTime)

        return """
            $lap${SPACE.repeat(FIVE)}$lapTime${SPACE.repeat(FIVE)}$overallTime
        """.trimIndent()
    }
}
