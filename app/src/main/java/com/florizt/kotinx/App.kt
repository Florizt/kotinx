package com.florizt.kotinx

import android.app.Activity
import android.app.Application
import com.florizt.base_mvvm_lib.base.AutoSize
import com.florizt.base_mvvm_lib.base.BaseContract
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
        startKoin { modules(allModule) }
    }

    override fun toForeground(activity: Activity) {
        super.toForeground(activity)
        println("==========toForeground==========")
    }

    override fun toBackground(activity: Activity) {
        super.toBackground(activity)
        println("==========toBackground==========")
    }

    override fun crashOpera(ex: Throwable) {
        super.crashOpera(ex)

    }
}