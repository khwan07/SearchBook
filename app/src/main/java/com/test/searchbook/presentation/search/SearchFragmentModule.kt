package com.test.searchbook.presentation.search

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.searchbook.di.ViewModelFactory
import com.test.searchbook.di.module.BaseFragmentModule
import com.test.searchbook.presentation.BookViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [BaseFragmentModule::class])
abstract class SearchFragmentModule {
    @Binds
    abstract fun bindFragment(fragment: SearchFragment): Fragment

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideBookViewModel(
            fragment: Fragment,
            viewModelFactory: ViewModelFactory
        ): BookViewModel {
            return ViewModelProvider(
                fragment.requireActivity(),
                viewModelFactory
            ).get(BookViewModel::class.java)
        }
    }
}