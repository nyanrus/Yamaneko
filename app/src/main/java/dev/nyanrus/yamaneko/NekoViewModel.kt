package dev.nyanrus.yamaneko

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mozilla.components.browser.state.store.BrowserStore

class NekoViewModel(val browserStore: BrowserStore): ViewModel() {
    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    fun setIsEditing(value: Boolean) {
        _isEditing.value = value
    }
}