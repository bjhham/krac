
const val ALPHABET_FIRST = 'a'
const val ALPHABET_LAST = 'z'
const val ALPHABET_SIZE = ALPHABET_LAST - ALPHABET_FIRST

val substringMatch: (String) -> (String) -> Boolean = { sub ->
    { str ->
        sub in str
    }
}

val defaultSplitWords: (String) -> Sequence<String> = { text ->
    sequence {
        val sb = StringBuilder(text.length)
        for (ch in text) {
            when(ch) {
                !in ALPHABET_FIRST..ALPHABET_LAST -> {
                    if (sb.isNotEmpty()) {
                        yield(sb.toString())
                        sb.clear()
                    }
                }
                else -> sb.append(ch)
            }
        }
        if (sb.isNotEmpty())
            yield(sb.toString())
    }
}