package com.test.searchbook.presentation.search

object QueryNormalizer {
    enum class Operator(val s: String) {
        OR("|"), EXCLUSIVE("-")
    }

    fun normalize(input: String): List<String> {
        var str = input
        val result = mutableListOf<String>()

        while (str.isNotEmpty()) {
            val orIndex = str.indexOf(Operator.OR.s).takeIf { it != -1 } ?: Int.MAX_VALUE
            val exclusiveIndex =
                str.indexOf(Operator.EXCLUSIVE.s).takeIf { it != -1 } ?: Int.MAX_VALUE

            if (str == Operator.OR.s) {
                result.add(str)
                break
            }

            if (result.size == 1) {
                result.add(str)
                break
            }

            if (orIndex < exclusiveIndex) {
                str.substring(0, orIndex).takeIf { it.isNotEmpty() }?.also { result.add(it) }
                str = str.substring(orIndex + 1, str.length)
            } else if (exclusiveIndex < orIndex) {
                val r = str.substring(0, exclusiveIndex).takeIf { it.isNotEmpty() }
                if (r != null) {
                    result.add(r)
                    str = str.substring(exclusiveIndex, str.length)
                } else {
                    val end = orIndex.coerceAtMost(str.length)
                    str.substring(exclusiveIndex, end).takeIf { it.isNotEmpty() }
                        ?.also { result.add(it) }
                    str = str.substring(end, str.length)
                }
            } else {
                result.add(str)
                str = ""
            }
        }
        return result
    }
}