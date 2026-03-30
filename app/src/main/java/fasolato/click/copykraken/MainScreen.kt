package fasolato.click.copykraken

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fasolato.click.copykraken.ui.theme.CopyKrakenTheme

@Composable
fun MainScreen(
    uiState: MainUiState,
    onArchive: () -> Unit,
    onRestoreFromHistory: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings_title)
                    )
                }
            }
        }
        item {
            Text(
                text = stringResource(R.string.app_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {
            Text(
                text = stringResource(R.string.current_text_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp, max = 240.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    Text(
                        text = uiState.currentText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        item {
            Text(
                text = stringResource(R.string.history_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (uiState.history.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(uiState.history.size) { index ->
                Card(
                    onClick = { onRestoreFromHistory(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = uiState.history[index],
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenEmptyPreview() {
    CopyKrakenTheme {
        MainScreen(uiState = MainUiState(), onArchive = {}, onRestoreFromHistory = {}, onSettingsClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenWithDataPreview() {
    CopyKrakenTheme {
        MainScreen(
            uiState = MainUiState(
                currentText = "Testo corrente accumulato\ncon una seconda riga",
                history = listOf("Primo blocco archiviato", "Secondo blocco archiviato")
            ),
            onArchive = {},
            onRestoreFromHistory = {},
            onSettingsClick = {}
        )
    }
}
