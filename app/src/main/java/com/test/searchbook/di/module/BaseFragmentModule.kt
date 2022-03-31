package com.test.searchbook.di.module

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class BaseFragmentModule {
    @Binds
    abstract fun bindLifecycleOwner(fragment: Fragment): LifecycleOwner
}