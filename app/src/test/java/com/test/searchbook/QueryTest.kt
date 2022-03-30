package com.test.searchbook

import com.test.searchbook.presentation.search.QueryNormalizer
import org.junit.Test

class QueryTest {
    @Test
    fun queryTest() {
        val qList = listOf(
            "android|ios",
            "android-ios",
            "android||ios",
            "android--ios",
            "|android-ios",
            "android-ios|",
            "android|ios-",
            "android|-ios",
            "android-|ios",
            "-android|ios",
            "and-roid|ios",
            "and-roid-ios",
            "|",
            "-",
            "||",
            "--",
        )
        for (str in qList) {
            val result = QueryNormalizer.normalize(str)
            println("q:$str")
            println("result:$result\n")
        }
    }
}