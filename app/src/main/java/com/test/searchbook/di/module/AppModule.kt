package com.test.searchbook.di.module

import android.app.Application
import com.test.searchbook.SearchBookApplication
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {
    @Binds
    abstract fun bindApplication(application: SearchBookApplication): Application
}