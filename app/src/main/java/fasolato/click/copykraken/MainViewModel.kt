package fasolato.click.copykraken

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val currentText: String = "",
    val history: List<String> = emptyList()
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _clipboardEvent = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val clipboardEvent: SharedFlow<String> = _clipboardEvent.asSharedFlow()

    fun onSharedText(text: String) {
        val current = _uiState.value.currentText
        val newText = if (current.isEmpty()) text else "$current\n$text"
        _uiState.update { it.copy(currentText = newText) }
        viewModelScope.launch { _clipboardEvent.emit(newText) }
    }

    fun archiveCurrent() {
        val current = _uiState.value.currentText.ifEmpty { return }
        _uiState.update { it.copy(currentText = "", history = listOf(current) + it.history) }
    }
}
