package com.florizt.base_mvvm_lib.base.repository.datasource.entity

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
/**
 * http请求返回数据基类
 * @param T
 * @property code String 错误码
 * @property data T 真实数据类型
 * @property msg String 错误信息
 * @constructor
 */
data class BaseResponse<T>(var code: String, var data: T, var msg: String)