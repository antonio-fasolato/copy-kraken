package fasolato.click.copykraken

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fasolato.click.copykraken.ui.theme.CopyKrakenTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CopyKrakenTheme {
                val uiState by viewModel.uiState.collectAsState()
                val maxHistorySize by viewModel.maxHistorySize.collectAsState()
                val showFullHistoryText by viewModel.showFullHistoryText.collectAsState()
                val autoArchiveMinutes by viewModel.autoArchiveMinutes.collectAsState()
                val context = LocalContext.current
                var showSettings by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    viewModel.clipboardEvent.collect { text ->
                        val clipboard = context.getSystemService(ClipboardManager::class.java)
                        clipboard.setPrimaryClip(
                            ClipData.newPlainText(context.getString(R.string.clipboard_label), text)
                        )
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (showSettings) {
                            TopAppBar(
                                title = { Text(stringResource(R.string.settings_title)) },
                                navigationIcon = {
                                    IconButton(onClick = { showSettings = false }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(R.string.navigate_back)
                                        )
                                    }
                                }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (!showSettings && uiState.currentText.isNotEmpty()) {
                            FloatingActionButton(onClick = viewModel::archiveCurrent) {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = stringResource(R.string.archive_button)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    if (showSettings) {
                        SettingsScreen(
                            maxHistorySize = maxHistorySize,
                            onMaxHistorySizeChange = viewModel::setMaxHistorySize,
                            showFullHistoryText = showFullHistoryText,
                            onShowFullHistoryTextChange = viewModel::setShowFullHistoryText,
                            autoArchiveMinutes = autoArchiveMinutes,
                            onAutoArchiveMinutesChange = viewModel::setAutoArchiveMinutes,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        MainScreen(
                            uiState = uiState,
                            onArchive = viewModel::archiveCurrent,
                            onRestoreFromHistory = viewModel::restoreFromHistory,
                            onSettingsClick = { showSettings = true },
                            showFullHistoryText = showFullHistoryText,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reload()
    }
}
