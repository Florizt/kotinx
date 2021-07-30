package com.florizt.base_mvvm_lib.base.repository.datasource.entity

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
/**
 * EventBus消息体格式
 * @property type Int 消息类型
 * @property src Any? 消息数据体
 * @property extra Map<String, Any>? 扩展字段
 * @constructor
 */
class MessageEvent(
    var type: Int,
    var src: Any?,
    var extra: Map<String, Any>?
) {
    constructor() : this(0, null, null)

    constructor(type: Int) : this(type, null, null)

    constructor(type: Int, src: Any?) : this(type, src, null)
}