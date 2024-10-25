package com.example.lunapic.di

import com.example.lunapic.ui.navigation.MyNavigator
import com.example.lunapic.ui.navigation.Navigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun bindNavigator(navigator : MyNavigator) : Navigator

}