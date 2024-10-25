package com.example.lunapic.ui.navigation

import android.os.Parcelable
import androidx.navigation.NavOptions

interface NavigationAction {
    val destination: Routes
    val parcelableArguments: Map<String, Parcelable>
        get() = emptyMap()
    val navOptions : NavOptions
        get() = NavOptions.Builder().build()
}