package com.test.searchbook.presentation.detail

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.searchbook.di.ViewModelFactory
import com.test.searchbook.di.module.BaseFragmentModule
import com.test.searchbook.presentation.BookDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [BaseFragmentModule::class])
abstract class BookDetailFragmentModule {
    @Binds
    abstract fun bindFragment(fragment: BookDetailFragment): Fragment

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideBookDetailViewModel(
            fragment: Fragment,
            viewModelFactory: ViewModelFactory
        ): BookDetailViewModel {
            return ViewModelProvider(
                fragment,
                viewModelFactory
            ).get(BookDetailViewModel::class.java)
        }
    }
}