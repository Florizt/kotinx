package com.florizt.base_mvvm_lib.base

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import com.florizt.base_mvvm_lib.ext.TYPE_DOCUMENT
import com.florizt.base_mvvm_lib.ext.createDir
import java.io.*
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    private var context: Context? = null
    private var defaultCrashHandler: Thread.UncaughtExceptionHandler? = null

    companion object {
        val instance: CrashHandler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CrashHandler()
        }
    }

    fun init(ctx: Context) {
        context = ctx.getApplicationContext()
        defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        try {
            var application: Application? = null
            var declaredMethod: Method? = null
            if (context is Application) {
                application = context as Application
                declaredMethod =
                    application.javaClass.getDeclaredMethod("crashOpera", Throwable::class.java)
            }
            //提供默认操作，导出异常信息到SD卡中
            //这里也可以上传异常信息到服务器，便于开发人员分析日志从而解决bug，自定义实现
            declaredMethod?.also { it.invoke(application, ex) } ?: dumpExceptionToSDCard(ex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //打印到控制台
        ex.printStackTrace()
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if (defaultCrashHandler != null) {
            defaultCrashHandler!!.uncaughtException(thread, ex)
        }
    }

    @Throws(IOException::class)
    private fun dumpExceptionToSDCard(ex: Throwable) {
        context?.also {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return
            }
            val current = System.currentTimeMillis()
            val time: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(current))
            val file = File(it.createDir(TYPE_DOCUMENT, "crash"), "crash_" + time + ".trace")
            val pw = PrintWriter(BufferedWriter(FileWriter(file)))
            pw.println(time)
            dumpPhoneInfo(pw)
            pw.println()
            ex.printStackTrace(pw)
            pw.close()
        }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun dumpPhoneInfo(pw: PrintWriter) {
        context?.also {
            val pm = it.packageManager
            val pi = pm.getPackageInfo(
                it.packageName,
                PackageManager.GET_ACTIVITIES
            )
            pw.print("App Version: ")
            pw.print(pi.versionName)
            pw.print('_')
            pw.println(pi.versionCode)
            //Android版本号
            pw.print("OS Version: ")
            pw.print(Build.VERSION.RELEASE)
            pw.print("_")
            pw.println(Build.VERSION.SDK_INT)
            //手机制造商
            pw.print("Vendor: ")
            pw.println(Build.MANUFACTURER)
            //手机型号
            pw.print("Model: ")
            pw.println(Build.MODEL)
            //CPU架构
            pw.print("CPU ABI: ")
            pw.println(Build.CPU_ABI)
        }
    }
}