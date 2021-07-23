package com.florizt.base_mvvm_lib.aop

import android.app.Activity
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityStart
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityStop
import com.florizt.base_mvvm_lib.base.ui.AppStackManager
import com.florizt.base_mvvm_lib.base.ui.BaseActivity
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect

/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
@Aspect
open class BackgroundAspect {
    @After(onActivityStart)
    open fun toForegroundAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            AppStackManager.instance.setAppCount(AppStackManager.instance.getAppCount() + 1)
            if (AppStackManager.instance.getRunInBackground()) {
                //应用从后台回到前台 需要做的操作
                AppStackManager.instance.setRunInBackground(false)
                val baseActivity = joinPoint.`this` as BaseActivity<*, *>
                val application = baseActivity.application
                val declaredMethod =
                    application.javaClass.getDeclaredMethod("toForeground", Activity::class.java)
                declaredMethod.invoke(application, baseActivity)
            }
        } catch (e: Exception) {

        }
    }

    @After(onActivityStop)
    open fun toBackgroundAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            AppStackManager.instance.setAppCount(AppStackManager.instance.getAppCount() - 1)
            if (AppStackManager.instance.getAppCount() == 0) {
                //应用进入后台 需要做的操作
                AppStackManager.instance.setRunInBackground(true)
                val baseActivity = joinPoint.`this` as BaseActivity<*, *>
                val application = baseActivity.application
                val declaredMethod =
                    application.javaClass.getDeclaredMethod("toBackground", Activity::class.java)
                declaredMethod.invoke(application, baseActivity)
            }
        } catch (e: Exception) {

        }
    }
}