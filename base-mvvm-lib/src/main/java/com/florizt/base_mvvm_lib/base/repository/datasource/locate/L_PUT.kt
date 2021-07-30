package com.florizt.base_mvvm_lib.base.repository.datasource.locate

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
/**
 * 类似OKHttp的@PUT
 * @property type LocalType 存储类型，目前只支持SharedPreferences存储
 * @property key Array<String> 要修改的key
 * @constructor
 * 注意：被该注解表示的方法，方法参数需要和key一一对应，不然会抛异常
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class L_PUT(val type: LocalType, val key: Array<String>)