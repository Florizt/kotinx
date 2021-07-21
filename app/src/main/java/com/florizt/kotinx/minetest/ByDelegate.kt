package com.florizt.kotinx

import kotlin.reflect.KProperty

/**
 * Created by wuwei
 * 2021/7/15
 * 佛祖保佑       永无BUG
 */
class ByDelegate {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>): Int {
        return 1
    }

    operator fun setValue(nothing: Nothing?, property: KProperty<*>, i: Int) {

    }
}