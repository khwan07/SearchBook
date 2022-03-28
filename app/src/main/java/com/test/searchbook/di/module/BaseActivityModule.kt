package com.test.searchbook.di.module

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class BaseActivityModule {
    @Binds
    abstract fun bindLifecycleOwner(fragmentActivity: FragmentActivity): LifecycleOwner

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideGlideModule(activity: FragmentActivity): RequestManager {
            return Glide.with(activity)
        }
    }
}