package com.example.lunapic.ui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class MyNavigator @Inject constructor() : Navigator {
    private val _actions : MutableStateFlow<NavigationAction?> by lazy {
        MutableStateFlow(null)
    }

    override val actions: StateFlow<NavigationAction?> = _actions.asStateFlow()

    override fun navigate(action: NavigationAction) {
        _actions.update { action }
    }
}