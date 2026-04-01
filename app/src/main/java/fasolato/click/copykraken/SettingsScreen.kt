package fasolato.click.copykraken

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fasolato.click.copykraken.ui.theme.CopyKrakenTheme

@Composable
fun SettingsScreen(
    maxHistorySize: Int,
    onMaxHistorySizeChange: (Int) -> Unit,
    showFullHistoryText: Boolean,
    onShowFullHistoryTextChange: (Boolean) -> Unit,
    autoArchiveMinutes: Int,
    onAutoArchiveMinutesChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember(maxHistorySize) { mutableStateOf(maxHistorySize.toString()) }
    var autoArchiveInput by remember(autoArchiveMinutes) { mutableStateOf(autoArchiveMinutes.toString()) }

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = inputText,
            onValueChange = { raw ->
                inputText = raw
                val parsed = raw.toIntOrNull()
                if (parsed != null && parsed >= 1) {
                    onMaxHistorySizeChange(parsed)
                }
            },
            label = { Text(stringResource(R.string.settings_max_history_label)) },
            supportingText = { Text(stringResource(R.string.settings_max_history_supporting)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = autoArchiveInput,
            onValueChange = { raw ->
                autoArchiveInput = raw
                val parsed = raw.toIntOrNull()
                if (parsed != null && parsed >= 1) {
                    onAutoArchiveMinutesChange(parsed)
                }
            },
            label = { Text(stringResource(R.string.settings_auto_archive_label)) },
            supportingText = { Text(stringResource(R.string.settings_auto_archive_supporting)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_show_full_history_label),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = showFullHistoryText,
                onCheckedChange = onShowFullHistoryTextChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    CopyKrakenTheme {
        SettingsScreen(
            maxHistorySize = 100,
            onMaxHistorySizeChange = {},
            showFullHistoryText = false,
            onShowFullHistoryTextChange = {},
            autoArchiveMinutes = 10,
            onAutoArchiveMinutesChange = {}
        )
    }
}
