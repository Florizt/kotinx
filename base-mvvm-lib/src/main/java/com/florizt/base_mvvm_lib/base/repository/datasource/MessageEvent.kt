package com.florizt.base_mvvm_lib.base.repository.datasource

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
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