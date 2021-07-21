package com.florizt.kotinx

import android.app.Application
import org.koin.core.context.startKoin

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { modules(allModule) }
    }
}