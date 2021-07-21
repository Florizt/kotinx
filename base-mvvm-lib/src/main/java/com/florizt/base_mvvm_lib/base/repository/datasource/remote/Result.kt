package com.florizt.base_mvvm_lib.base.repository.datasource.remote

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any> constructor(val data: T) : Result<T>()
    data class Failed constructor(val exception: ResultException) : Result<Nothing>()
}