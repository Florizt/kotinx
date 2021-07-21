package com.florizt.base_mvvm_lib.base.repository.datasource.locate

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class L_GET(val type: LocalType, val key: Array<String>)