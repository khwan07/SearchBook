package com.test.searchbook.di.module

import com.test.searchbook.presentation.detail.BookDetailFragment
import com.test.searchbook.presentation.detail.BookDetailFragmentModule
import com.test.searchbook.presentation.search.SearchFragment
import com.test.searchbook.presentation.search.SearchFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector(modules = [SearchFragmentModule::class])
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector(modules = [BookDetailFragmentModule::class])
    abstract fun contributeBookDetailFragment(): BookDetailFragment
}