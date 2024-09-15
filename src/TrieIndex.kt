/**
 * Trie index of bound depth.  Each edge in the tree represents a letter in a word.  Leaf nodes in the tree
 * contain a bucket of matching values.
 *
 * @property depth how deep the tree goes
 * @property words function for spliting an arbitrary string into words
 */
class TrieIndex<E>(
    private val depth: Int,
    private val words: (E) -> Sequence<String>,
    private val match: (String) -> (E) -> Boolean
): AutoCompleteIndex<E> {
    init {
        check(depth > 0) { "depth must be greater than 1" }
    }

    private val root = TrieElem()

    override fun add(value: E) {
        for (word in words(value))
            putWordMatch(word, value)
    }

    private fun putWordMatch(word: String, value: E): Boolean {
        var n: TrieElem = root
        for (i in 0 until depth)
            n = n.getOrCreate(word.offsetAt(i), ::TrieElem)
        return n.getOrCreate(word.offsetAt(depth), ::TrieLeaf).add(value)
    }

    override fun search(prefix: String): Iterable<E> =
        defaultSplitWords(prefix).map { word ->
            getWordMatches(word)
        }.intersectAll()

    private fun getWordMatches(word: String): Set<E> {
        var n: TrieElem = root
        for (i in word.indices) {
            val offset = word.offsetAt(i)
            n = when(val child = n[offset]) {
                is TrieElem -> child
                is TrieLeaf -> return child.values.filterTo(mutableSetOf(), match(word))
                null -> return emptySet()
                else -> error("Unexpected trie node ${child::class}")
            }
        }
        return n.values()
    }

    abstract inner class TrieNode {
        abstract fun values(): Set<E>
    }

    inner class TrieElem(val children: Array<TrieNode?>): TrieNode() {
        constructor(): this(arrayOfNulls(ALPHABET_SIZE + 1))

        inline fun <reified N: TrieNode> getOrCreate(i: Int, create: () -> N) =
            when(val child = children[i]) {
                null -> create().also {
                    children[i] = it
                }
                else -> child as N
            }

        operator fun get(i: Int): TrieNode? =
            children[i]

        override fun values(): Set<E> =
            children.asSequence()
                .filterNotNull()
                .map { it.values() }
                .mergeAll()

        override fun toString(): String = toString(0)

        private fun toString(depth: Int): String = buildString {
            if (depth != 0)
                append('\n')

            val indent = String(CharArray(depth) { ' ' })
            for (i in 0..ALPHABET_SIZE) {
                children[i]?.let { child ->
                    append(indent)
                    append(if (i == ALPHABET_SIZE) '_' else ALPHABET_FIRST + i)
                    append(':')
                    when(child) {
                        is TrieElem -> append(child.toString(depth + 2))
                        is TrieLeaf -> append(' ', child.values, '\n')
                        else -> error("Unexpected trie node ${child::class}")
                    }
                }
            }
        }

    }

    inner class TrieLeaf(internal val values: MutableSet<E>): TrieNode() {
        constructor(): this(mutableSetOf())

        override fun values(): Set<E> =
            values

        fun add(value: E) =
            values.add(value)

    }

    private fun String.offsetAt(i: Int): Int {
        return if (i >= length) {
            ALPHABET_SIZE
        } else {
            val ch = this[i]
            check(ch in ALPHABET_FIRST..ALPHABET_LAST) { "Character outside range $ALPHABET_FIRST..$ALPHABET_LAST" }
            return ch - ALPHABET_FIRST
        }
    }

    override fun toString(): String =
        root.toString()

}