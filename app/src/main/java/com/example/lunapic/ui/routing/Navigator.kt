package com.example.lunapic.ui.routing

import kotlinx.coroutines.flow.StateFlow

interface Navigator {
    val actions : StateFlow<NavigationAction?>
    fun navigate(action : NavigationAction)
}