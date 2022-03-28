package com.test.searchbook.presentation

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.test.searchbook.di.ViewModelFactory
import com.test.searchbook.di.module.BaseActivityModule
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [BaseActivityModule::class])
abstract class MainActivityModule {
    @Binds
    abstract fun bindActivity(activity: MainActivity): FragmentActivity

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideBookViewModel(activity: FragmentActivity, viewModelFactory: ViewModelFactory): BookViewModel {
            return ViewModelProvider(activity, viewModelFactory).get(BookViewModel::class.java)
        }
    }
}