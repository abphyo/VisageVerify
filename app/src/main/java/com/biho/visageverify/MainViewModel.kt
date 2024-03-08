package com.biho.visageverify

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable

@OptIn(SavedStateHandleSaveableApi::class)
class MainViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val visiblePermissionDialogQueue = mutableStateListOf<String>()

    var permissionDialogState by savedStateHandle.saveable {
        mutableStateOf(false)
    }

    fun showDialog() {
        permissionDialogState = true
    }

    fun dismissDialog() {
        permissionDialogState = false
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

}