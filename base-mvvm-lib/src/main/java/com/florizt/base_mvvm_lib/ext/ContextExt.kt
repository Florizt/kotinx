package com.florizt.base_mvvm_lib.ext

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */

fun Context.showSoftKeyboard(v: View) {
    val systemService = getSystemService(Context.INPUT_METHOD_SERVICE)
    if (systemService is InputMethodManager) {
        systemService.showSoftInput(v, InputMethodManager.SHOW_FORCED)
    }
}

fun Context.hiddenSoftKeyboard(v: View) {
    val systemService = getSystemService(Context.INPUT_METHOD_SERVICE)
    if (systemService is InputMethodManager) {
        systemService.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun Context.getString(id: Int): String = getString(id)

fun Context.getString(id: Int, vararg formatArgs: Any?) = getString(id, formatArgs)

fun Context.getColor(id: Int): Int = resources.getColor(id)

fun Context.getDimen(id: Int): Int = resources.getDimensionPixelOffset(id)

@Synchronized
fun Context.getSharedPreferences(): SharedPreferences =
    getSharedPreferences(packageName, Context.MODE_PRIVATE)

fun Context.getDrawable(id: Int, mutate: Boolean): Drawable {
    return if (mutate) {
        resources.getDrawable(id).mutate()
    } else {
        resources.getDrawable(id)
    }
}

fun Context.getDrawableUri(id: Int): String {
    return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            resources.getResourcePackageName(id) + "/" +
            resources.getResourceTypeName(id) + "/" +
            resources.getResourceEntryName(id)
}

fun Context.getAsstManager(): AssetManager = assets

fun Context.openAssets(fileName: String): InputStream = getAsstManager().open(fileName)

fun Context.openRaw(id: Int): InputStream = resources.openRawResource(id)

fun Context.readAsset(fileName: String): List<String> {
    val list: ArrayList<String> = arrayListOf()
    val inputStreamReader = InputStreamReader(openAssets(fileName), "UTF-8")
    val bufferedReader = BufferedReader(inputStreamReader)
    var out: String = ""
    while (bufferedReader.readLine()?.also { out = it } != null) {
        list.add(out)
    }
    return list
}

fun Context.readRaw(id: Int): List<String> {
    val list = arrayListOf<String>()
    val inputStreamReader = InputStreamReader(openRaw(id), "UTF-8")
    val bufferedReader = BufferedReader(inputStreamReader)
    var out: String = ""
    while (bufferedReader.readLine()?.also { out = it } != null) {
        list.add(out)
    }
    return list
}

fun Context.getDensity() = resources.displayMetrics.density

fun Context.dp2px(dp: Float): Int = (dp * getDensity() + 0.5f).toInt()

fun Context.px2dp(px: Int): Int = (px / getDensity() + 0.5f).toInt()

fun Context.sp2px(sp: Float): Int = (sp * getDensity() + 0.5f).toInt()

fun Context.px2sp(px: Float): Int = (px / getDensity() + 0.5f).toInt()