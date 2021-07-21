package com.florizt.kotinx.minetest

import android.view.View

/**
 * Created by wuwei
 * 2021/7/15
 * 佛祖保佑       永无BUG
 */
fun String?.empty(): Boolean {
    return if (this == null || this.length <= 0) true else false
}

fun View.setOnClickListeners(l1: (View) -> Boolean, l2: Int.(View) -> Boolean) :Boolean{
//    this.l1(this)

    l1(this)

    let(l1)

    let { l1(this) }

    apply { l1(this) }

//    this.l2(1)

    l2(1,this)

//    let(l2)

    let { l2(1,this) }

    apply { l2(1,this) }

    return l1(this)
}