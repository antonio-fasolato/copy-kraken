package fasolato.click.copykraken

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class ShareReceiverActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedText = intent
            ?.takeIf { it.action == Intent.ACTION_SEND && it.type == "text/plain" }
            ?.getStringExtra(Intent.EXTRA_TEXT)

        if (!sharedText.isNullOrEmpty()) {
            val newText = AppStorage(applicationContext).appendText(sharedText)
            val clipboard = getSystemService(ClipboardManager::class.java)
            clipboard.setPrimaryClip(
                ClipData.newPlainText(getString(R.string.clipboard_label), newText)
            )
            Toast.makeText(this, R.string.share_received_toast, Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
