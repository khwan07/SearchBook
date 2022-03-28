package com.test.searchbook.di.component

import com.test.searchbook.SearchBookApplication
import com.test.searchbook.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        BookApiModule::class,
        ActivityModule::class,
        FragmentModule::class,
        ViewModelModule::class,
    ]
)
interface AppComponent : AndroidInjector<SearchBookApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: SearchBookApplication): Builder

        fun build(): AppComponent
    }

    override fun inject(instance: SearchBookApplication?)
}