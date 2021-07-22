package com.florizt.base_mvvm_lib.ext

import android.app.Activity
import android.os.Build
import android.view.DisplayCutout

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
fun Activity.hideSoftKeyBoard() {
    currentFocus?.also { hiddenSoftKeyboard(it) }
}

/**
 * Android P 刘海屏判断
 *
 * @param activity
 * @return
 */
fun Activity.isAndroidPNotch(): DisplayCutout? {
    val decorView = window.decorView
    if (Build.VERSION.SDK_INT >= 28) {
        val windowInsets = decorView.rootWindowInsets
        if (windowInsets != null) return windowInsets.displayCutout
    }
    return null
}

/**
 * 判断是否是刘海屏
 *
 * @return
 */
fun Activity.hasNotchScreen(): Boolean {
    return if (hasNotchAtXiaomi() || hasNotchAtHuawei() || hasNotchAtOPPO()
        || hasNotchAtVivo() || isAndroidPNotch() != null
    ) true else false
}