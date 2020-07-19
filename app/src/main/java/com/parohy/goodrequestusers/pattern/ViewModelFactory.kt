package com.parohy.goodrequestusers.pattern

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

interface ViewModelFactory<out VM: ViewModel> {
    fun create(handle: SavedStateHandle): VM
}

class GenericSavedStateViewModelFactory<out V: ViewModel>(
    private val viewModelFactory: ViewModelFactory<V>,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
): AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return viewModelFactory.create(handle) as T
    }
}

@MainThread
inline fun <reified VM: ViewModel> SavedStateRegistryOwner.withFactory(
    factory: ViewModelFactory<VM>,
    defaultArgs: Bundle? = null
) = GenericSavedStateViewModelFactory(
    factory,
    this,
    defaultArgs
)
