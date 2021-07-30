package com.florizt.base_mvvm_lib.base.repository.datasource.locate

import android.content.Context

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
/**
 * 工厂方式获取[Localfit]
 * 可通过koin进行注入
 */
object LocalfitFactory {
    fun <S> getService(context: Context, service: Class<S>): S {
        return getService(context, service, null)
    }

    fun <S> getService(
        context: Context,
        service: Class<S>,
        psw: String?
    ): S {
        return Localfit.Builder(context, psw)
            .build()
            .create(service)
    }
}