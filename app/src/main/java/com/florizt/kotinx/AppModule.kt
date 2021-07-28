package com.florizt.kotinx

import com.florizt.base_mvvm_lib.base.repository.datasource.locate.LocalfitFactory
import com.florizt.base_mvvm_lib.base.repository.datasource.remote.RetrofitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
val viewModelModule = module {
    viewModel { TestViewModel(get()) }
}

val repositoryModule = module {
    single { TestRepository(get()) }
}

val modelModule = module {
    single { TestModel(get(), get()) }
}

val serviceModule = module {
    single { RetrofitFactory.getService(ITestRemoteService::class.java) }
    single { LocalfitFactory.getService(androidContext(), ITestLocalService::class.java) }
}

val allModule = listOf(
    viewModelModule,
    repositoryModule,
    modelModule,
    serviceModule
)

