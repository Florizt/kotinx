package com.florizt.base_mvvm_lib.ext

import android.app.Activity

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
fun Activity.hideSoftKeyBoard() {
    currentFocus?.also { hiddenSoftKeyboard(it) }
}