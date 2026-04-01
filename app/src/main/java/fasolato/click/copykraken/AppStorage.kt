package fasolato.click.copykraken

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class AppStorage(context: Context) {
    private val prefs = context.getSharedPreferences("copykraken", Context.MODE_PRIVATE)

    var currentText: String
        get() = prefs.getString(KEY_CURRENT, "") ?: ""
        set(value) { prefs.edit().putString(KEY_CURRENT, value).apply() }

    var history: List<HistoryEntry>
        get() {
            val raw = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
            val arr = JSONArray(raw)
            return List(arr.length()) { i ->
                when (val element = arr.get(i)) {
                    is JSONObject -> HistoryEntry(
                        text = element.getString("text"),
                        timestamp = element.getLong("timestamp")
                    )
                    // migration: old format stored plain strings
                    else -> HistoryEntry(text = arr.getString(i), timestamp = 0L)
                }
            }
        }
        set(value) {
            val arr = JSONArray(value.map { entry ->
                JSONObject().apply {
                    put("text", entry.text)
                    put("timestamp", entry.timestamp)
                }
            })
            prefs.edit().putString(KEY_HISTORY, arr.toString()).apply()
        }

    var maxHistorySize: Int
        get() = prefs.getInt(KEY_MAX_HISTORY, 100)
        set(value) { prefs.edit().putInt(KEY_MAX_HISTORY, value).apply() }

    var showFullHistoryText: Boolean
        get() = prefs.getBoolean(KEY_SHOW_FULL_HISTORY, false)
        set(value) { prefs.edit().putBoolean(KEY_SHOW_FULL_HISTORY, value).apply() }

    var autoArchiveMinutes: Int
        get() = prefs.getInt(KEY_AUTO_ARCHIVE_MINUTES, 10)
        set(value) { prefs.edit().putInt(KEY_AUTO_ARCHIVE_MINUTES, value).apply() }

    var currentTextTimestamp: Long
        get() = prefs.getLong(KEY_CURRENT_TIMESTAMP, 0L)
        set(value) { prefs.edit().putLong(KEY_CURRENT_TIMESTAMP, value).apply() }

    fun appendText(text: String): String {
        val now = System.currentTimeMillis()
        val existing = currentText
        if (existing.isNotEmpty()) {
            val ageMillis = now - currentTextTimestamp
            val thresholdMillis = autoArchiveMinutes * 60_000L
            if (ageMillis >= thresholdMillis) {
                history = (listOf(HistoryEntry(existing, currentTextTimestamp)) + history).take(maxHistorySize)
                currentText = ""
                currentTextTimestamp = 0L
            }
        }
        val base = currentText
        val newText = if (base.isEmpty()) text else "$base\n$text"
        currentText = newText
        if (currentTextTimestamp == 0L) currentTextTimestamp = now
        return newText
    }

    companion object {
        private const val KEY_CURRENT = "currentText"
        private const val KEY_HISTORY = "history"
        private const val KEY_MAX_HISTORY = "maxHistorySize"
        private const val KEY_SHOW_FULL_HISTORY = "showFullHistoryText"
        private const val KEY_AUTO_ARCHIVE_MINUTES = "autoArchiveMinutes"
        private const val KEY_CURRENT_TIMESTAMP = "currentTextTimestamp"
    }
}
