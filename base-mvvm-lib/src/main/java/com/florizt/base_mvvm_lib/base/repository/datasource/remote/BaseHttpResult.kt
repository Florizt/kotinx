package com.florizt.base_mvvm_lib.base.repository.datasource.remote

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
data class BaseHttpResult<T>(var code: String, var data: T, var msg: String)