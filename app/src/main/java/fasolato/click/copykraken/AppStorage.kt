package fasolato.click.copykraken

import android.content.Context
import org.json.JSONArray

class AppStorage(context: Context) {
    private val prefs = context.getSharedPreferences("copykraken", Context.MODE_PRIVATE)

    var currentText: String
        get() = prefs.getString(KEY_CURRENT, "") ?: ""
        set(value) { prefs.edit().putString(KEY_CURRENT, value).apply() }

    var history: List<String>
        get() {
            val raw = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
            val arr = JSONArray(raw)
            return List(arr.length()) { arr.getString(it) }
        }
        set(value) {
            prefs.edit().putString(KEY_HISTORY, JSONArray(value).toString()).apply()
        }

    fun appendText(text: String): String {
        val newText = currentText.let { if (it.isEmpty()) text else "$it\n$text" }
        currentText = newText
        return newText
    }

    companion object {
        private const val KEY_CURRENT = "currentText"
        private const val KEY_HISTORY = "history"
    }
}
