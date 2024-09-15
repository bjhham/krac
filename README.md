Kotlin Robust Auto-complete
============================

This library provides tools for auto-complete implementations in Kotlin.  This can be useful for when you need an efficient auto-complete but don't want a full database.

### Trie Index

```kotlin
val index = AutoCompleteIndex.trie(depth = 4)
```

This will create an index that matches on the first 4 characters typed, then uses a substring check on the remainder.  The keys are always strings, but the values can be anything.