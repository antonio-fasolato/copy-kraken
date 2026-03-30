package fasolato.click.copykraken

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
    modifier: Modifier = Modifier
) {
    var inputText by remember(maxHistorySize) { mutableStateOf(maxHistorySize.toString()) }

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
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    CopyKrakenTheme {
        SettingsScreen(maxHistorySize = 100, onMaxHistorySizeChange = {})
    }
}
