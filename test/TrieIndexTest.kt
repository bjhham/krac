import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TrieIndexTest {

    private val index = AutoCompleteIndex.trie(3).apply {
        for (artifact in readArtifacts())
            add(artifact)
    }

    @Test
    fun `word split`() {
        assertEquals(
            "org, springframework, boot, spring, boot, starter, web",
            defaultSplitWords.invoke("org.springframework.boot:spring-boot-starter-web:3.0.0").joinToString()
        )
    }

    @Test
    fun `two char search hit`() =
        index.expectResults("sq", """
            com.microsoft.sqlserver:mssql-jdbc:9.2.1.jre8
            com.microsoft.sqlserver:mssql-jdbc:9.4.1.jre8
            com.squareup.okhttp3:okhttp:4.9.3
            com.squareup.retrofit2:retrofit:2.9.0
            com.squareup.moshi:moshi:1.12.0
            com.squareup.leakcanary:leakcanary-android:2.10
            com.squareup.retrofit2:converter-jackson:2.9.0
            com.squareup.okio:okio:2.10.0
            com.squareup.okhttp3:logging-interceptor:4.9.3
        """.trimIndent())


    @Test
    fun `two char search miss`() =
        index.expectResults("zz", "")

    @Test
    fun `three char search hit`() =
        index.expectResults("hib", """
            org.hibernate:hibernate-core:6.1.3.Final
            com.vladmihalcea:hibernate-types-52:2.19.2
            org.hibernate:hibernate-envers:6.1.3.Final
            org.hibernate:hibernate-entitymanager:6.1.3.Final
            org.hibernate:hibernate-validator:7.0.4.Final
            org.hibernate:hibernate-spatial:6.1.3.Final
            org.hibernate:hibernate-c3p0:6.1.3.Final
            org.hibernate:hibernate-ehcache:6.1.3.Final
            org.hibernate:hibernate-proxool:6.1.3.Final
        """)

    @Test
    fun `three char search miss`() =
        index.expectResults("qrl", "")

    @Test
    fun `four char search hit`() =
        index.expectResults("enve", """
            org.hibernate:hibernate-envers:6.1.3.Final
        """)

    @Test
    fun `four char search miss`() =
        index.expectResults("envt", "")

    @Test
    fun `five char search hit`() =
        index.expectResults("enver", """
        org.hibernate:hibernate-envers:6.1.3.Final
    """)

    @Test
    fun `five char search miss`() =
        index.expectResults("envek", "")

    private fun AutoCompleteIndex<String>.expectResults(search: String, expected: String) {
        val actual = search(search).joinToString("\n")
        assertEquals(expected.trimIndent(), actual, "Expected results for $search do not match")
    }

    private fun readArtifacts(): Sequence<String> {
        val resourceAsStream = TrieIndexTest::class.java.getResourceAsStream("top250artifacts.txt")
                ?: fail("top250artifacts.txt not found")
        return resourceAsStream.reader().buffered().lineSequence()
    }

}