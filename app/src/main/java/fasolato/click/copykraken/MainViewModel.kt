package fasolato.click.copykraken

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val storage = AppStorage(app)

    private val _clipboardEvent = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val clipboardEvent: SharedFlow<String> = _clipboardEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(
        MainUiState(currentText = storage.currentText, history = storage.history)
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _maxHistorySize = MutableStateFlow(storage.maxHistorySize)
    val maxHistorySize: StateFlow<Int> = _maxHistorySize.asStateFlow()

    fun setMaxHistorySize(value: Int) {
        if (value < 1) return
        storage.maxHistorySize = value
        _maxHistorySize.value = value
        val trimmed = _uiState.value.history.take(value)
        if (trimmed.size < _uiState.value.history.size) {
            storage.history = trimmed
            _uiState.update { it.copy(history = trimmed) }
        }
    }

    fun reload() {
        _uiState.update { MainUiState(currentText = storage.currentText, history = storage.history) }
    }

    fun archiveCurrent() {
        val current = _uiState.value.currentText.ifEmpty { return }
        val newHistory = (listOf(current) + _uiState.value.history).take(storage.maxHistorySize)
        storage.currentText = ""
        storage.history = newHistory
        _uiState.update { MainUiState(currentText = "", history = newHistory) }
    }

    fun restoreFromHistory(index: Int) {
        val state = _uiState.value
        val restored = state.history.getOrNull(index) ?: return
        val remaining = state.history.toMutableList().also { it.removeAt(index) }
        val newHistory = if (state.currentText.isEmpty()) remaining
                         else listOf(state.currentText) + remaining
        storage.currentText = restored
        storage.history = newHistory
        _uiState.update { MainUiState(currentText = restored, history = newHistory) }
        viewModelScope.launch { _clipboardEvent.emit(restored) }
    }
}
