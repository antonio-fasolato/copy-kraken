package fasolato.click.copykraken

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fasolato.click.copykraken.ui.theme.CopyKrakenTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent?.getSharedText()?.let { viewModel.onSharedText(it) }

        setContent {
            CopyKrakenTheme {
                val uiState by viewModel.uiState.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    viewModel.clipboardEvent.collect { text ->
                        val clipboard = context.getSystemService(ClipboardManager::class.java)
                        val clip = ClipData.newPlainText(
                            context.getString(R.string.clipboard_label),
                            text
                        )
                        clipboard.setPrimaryClip(clip)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        uiState = uiState,
                        onArchive = viewModel::archiveCurrent,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getSharedText()?.let { viewModel.onSharedText(it) }
    }

    private fun Intent.getSharedText(): String? =
        takeIf { action == Intent.ACTION_SEND && type == "text/plain" }
            ?.getStringExtra(Intent.EXTRA_TEXT)
}
