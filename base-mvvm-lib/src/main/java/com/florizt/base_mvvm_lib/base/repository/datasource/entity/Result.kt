package com.florizt.base_mvvm_lib.base.repository.datasource.entity

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
/**
 * http请求和本地数据请求结果封装处理
 * @param out T : Any
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any> constructor(val data: T) : Result<T>()
    data class Failed constructor(val exception: ResultException) : Result<Nothing>()
}