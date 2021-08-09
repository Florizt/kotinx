package com.florizt.base_mvvm_lib.ext

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.DisplayCutout
import android.view.WindowManager
import androidx.core.content.FileProvider
import java.io.File
import java.lang.reflect.Method


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

fun Activity.getScreenWidth(): Int {
    val wm = getSystemService(Activity.WINDOW_SERVICE) as WindowManager
    val metrics = DisplayMetrics()
    wm.defaultDisplay.getRealMetrics(metrics)
    return metrics.widthPixels
}

fun Activity.getScreenHeight(): Int {
    val wm = getSystemService(Activity.WINDOW_SERVICE) as WindowManager
    val metrics = DisplayMetrics()
    wm.defaultDisplay.getRealMetrics(metrics)
    return metrics.heightPixels
}

fun Activity.getStatusBarHeight(): Int {
    var height = 0
    height = if (hasNotchAtXiaomi()) {
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        resources.getDimensionPixelSize(resourceId)
    } else if (hasNotchAtHuawei()) {
        getNotchSizeAtHuawei().get(1)
    } else if (hasNotchAtVivo()) {
        dp2px(27f)
    } else if (hasNotchAtOPPO()) {
        80
    } else {
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        resources.getDimensionPixelSize(resourceId)
    }
    return height
}

/**
 * 是否是小米刘海屏
 *
 * @return
 */
fun Activity.hasNotchAtXiaomi(): Boolean {
    return try {
        val intMethod: Method = Class.forName("android.os.SystemProperties")
            .getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
        val invoke = intMethod.invoke(null, "ro.miui.notch", 0) as Int
        invoke == 1
    } catch (e: Exception) {
        false
    }
}

/**
 * 是否是华为刘海屏
 *
 * @return
 */
fun Activity.hasNotchAtHuawei(): Boolean {
    return try {
        val classLoader = classLoader
        val HwNotchSizeUtil =
            classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil")
        val get: Method = HwNotchSizeUtil.getMethod("hasNotchInScreen")
        get.invoke(HwNotchSizeUtil) as Boolean
    } catch (e: Exception) {
        false
    }
}

/**
 * 华为获取刘海尺寸：width、height
 * int[0]值为刘海宽度 int[1]值为刘海高度
 *
 * @return
 */
fun Activity.getNotchSizeAtHuawei(): IntArray {
    return try {
        val cl = classLoader
        val HwNotchSizeUtil =
            cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
        val get: Method = HwNotchSizeUtil.getMethod("getNotchSize")
        get.invoke(HwNotchSizeUtil) as IntArray
    } catch (e: Exception) {
        intArrayOf(0, 0)
    }
}

/**
 * 是否是vivo刘海屏
 *
 * @return
 */
fun Activity.hasNotchAtVivo(): Boolean {
    return try {
        val classLoader = classLoader
        val FtFeature = classLoader.loadClass("android.util.FtFeature")
        val method: Method = FtFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
        method.invoke(FtFeature, 0x00000020) as Boolean
    } catch (e: Exception) {
        false
    }
}

/**
 * 是否是oppo刘海屏
 *
 * @return
 */
fun Activity.hasNotchAtOPPO(): Boolean {
    return packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
}

/**
 * 安装apk
 *
 * 需要权限：
 *  <!-- 读写文件 -->
 *  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 *  <!--安装apk需要-->
 *  <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
 *
 *
 * 需要FileProvider：
 *  <provider
 *      android:name="androidx.core.content.FileProvider"
 *      android:authorities="${applicationId}.fileprovider"
 *      android:exported="false"
 *      android:grantUriPermissions="true">
 *      <meta-data
 *          android:name="android.support.FILE_PROVIDER_PATHS"
 *          android:resource="@xml/file_paths" />
 *  </provider>
 *
 *
 * 需要@xml/file_paths：
 * <?xml version="1.0" encoding="utf-8"?>
 *  <resources>
 *      <paths>
 *          <root-path
 *              name="camera_photos"
 *              path="" />
 *          <external-path
 *              name="files_root"
 *              path="download" />
 *          <external-path
 *              name="external_storage_root"
 *              path="." />
 *      </paths>
 *
 * @receiver Activity
 * @param file File
 */
fun Activity.installApk(file: File) {
    try {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri: Uri = FileProvider.getUriForFile(
                this,
                packageName.toString() + ".fileprovider",
                file
            )
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}