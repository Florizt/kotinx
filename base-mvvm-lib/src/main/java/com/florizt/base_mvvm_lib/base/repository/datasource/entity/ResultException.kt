package com.florizt.base_mvvm_lib.base.repository.datasource.entity

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
/**
 * http请求当不是请求成功错误码时，抛出的自定义异常
 * @property code String?
 * @property msg String?
 * @constructor
 */
class ResultException(var code: String?, var msg: String?) : Exception(msg)