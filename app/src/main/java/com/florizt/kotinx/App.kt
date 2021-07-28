package com.florizt.kotinx

import android.app.Application
import com.florizt.base_mvvm_lib.base.AutoSize
import com.florizt.base_mvvm_lib.base.BaseContract
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
class App : Application(), BaseContract.IApplication {
    @AutoSize(designWidthInDp = 375, designHeightInDp = 664)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App.applicationContext)
            modules(allModule)
        }
    }
}