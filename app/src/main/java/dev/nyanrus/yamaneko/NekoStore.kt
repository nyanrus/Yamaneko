package dev.nyanrus.yamaneko

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class NekoStore {
    var isEditing = false
}

@Composable
fun makeNekoStore(): NekoStore {
    val store = remember {
        NekoStore()
    }
    return store
}