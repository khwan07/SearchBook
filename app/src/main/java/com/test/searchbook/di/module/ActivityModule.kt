package com.test.searchbook.di.module

import com.test.searchbook.presentation.MainActivity
import com.test.searchbook.presentation.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun provideMainActivityModule(): MainActivity
}