package com.test.searchbook.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.searchbook.di.ViewModelFactory
import com.test.searchbook.di.ViewModelKey
import com.test.searchbook.presentation.BookViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(BookViewModel::class)
    abstract fun bindBookViewModel(bookViewModel: BookViewModel): ViewModel
}