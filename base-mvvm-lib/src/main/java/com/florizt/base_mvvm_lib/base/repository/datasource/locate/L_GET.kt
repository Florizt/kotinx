package com.florizt.base_mvvm_lib.base.repository.datasource.locate

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
/**
 * 类似OKHttp的@GET
 * @property type LocalType 存储类型，目前只支持SharedPreferences存储
 * @property key Array<String> 要获取的key
 * @constructor
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class L_GET(val type: LocalType, val key: Array<String>)