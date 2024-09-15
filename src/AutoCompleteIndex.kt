/**
 * Simple index interface for finding items with the given prefix.
 */
interface AutoCompleteIndex<E> {
    companion object {
        /**
         * Create a Trie String index with the given tree depth (checks first n characters).
         */
        fun trie(depth: Int): TrieIndex<String> =
            TrieIndex(depth, defaultSplitWords, substringMatch)
    }

    /**
     * Add an item to the index.
     */
    fun add(value: E)

    /**
     * Find all items starting with the given prefix.
     */
    fun search(prefix: String): Iterable<E>
}