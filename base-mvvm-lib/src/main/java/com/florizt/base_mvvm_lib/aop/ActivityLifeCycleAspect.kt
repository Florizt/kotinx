package com.florizt.base_mvvm_lib.aop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityCreate
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityDestroy
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityNewIntent
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityPause
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityResult
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityResume
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivitySaveInstanceState
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityStart
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityStop
import com.florizt.base_mvvm_lib.base.ui.BaseActivity
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect

/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
/**
 * 对外开放的Activity生命周期
 * 可通过在Application里实现 [com.florizt.base_mvvm_lib.base.BaseContract.IApplication]，重写其生命周期方法
 * 但不建议这种，因为功能不分离，耦合性强
 * 建议仿照 [BarAspect]，使用切面来处理
 */
@Aspect
open class ActivityLifeCycleAspect {
    @After(onActivityCreate)
    open fun onActivityCreateAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application

            val declaredMethod =
                application.javaClass.getDeclaredMethod(
                    "onActivityCreate",
                    Activity::class.java,
                    Bundle::class.java
                )
            declaredMethod.invoke(application, baseActivity, joinPoint.args[0])
        } catch (e: Exception) {

        }
    }

    @After(onActivityNewIntent)
    open fun onActivityNewIntentAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application
            val declaredMethod =
                application.javaClass.getDeclaredMethod(
                    "onActivityNewIntent",
                    Activity::class.java,
                    Intent::class.java
                )
            declaredMethod.invoke(application, baseActivity, joinPoint.args[0])
        } catch (e: Exception) {

        }
    }

    @After(onActivitySaveInstanceState)
    open fun onActivitySaveInstanceStateAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application
            val declaredMethod =
                application.javaClass.getDeclaredMethod(
                    "onActivitySaveInstanceState",
                    Activity::class.java,
                    Bundle::class.java
                )
            declaredMethod.invoke(application, baseActivity, joinPoint.args[0])
        } catch (e: Exception) {

        }
    }

    @After(onActivityResult)
    open fun onActivityResultAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application
            val declaredMethod =
                application.javaClass.getDeclaredMethod(
                    "onActivityResult",
                    Activity::class.java,
                    Int::class.java,
                    Int::class.java,
                    Intent::class.java
                )
            declaredMethod.invoke(
                application, baseActivity,
                joinPoint.args[0],
                joinPoint.args[1],
                joinPoint.args[2]
            )
        } catch (e: Exception) {

        }
    }

    @After(onActivityStart)
    open fun onActivityStartAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application
            val declaredMethod =
                application.javaClass.getDeclaredMethod("onActivityStart", Activity::class.java)
            declaredMethod.invoke(application, baseActivity)
        } catch (e: Exception) {

        }
    }

    @After(onActivityResume)
    open fun onActivityResumeAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application
            val declaredMethod =
                application.javaClass.getDeclaredMethod("onActivityResume", Activity::class.java)
            declaredMethod.invoke(application, baseActivity)
        } catch (e: Exception) {

        }
    }

    @After(onActivityPause)
    open fun onActivityPauseAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application
            val declaredMethod =
                application.javaClass.getDeclaredMethod("onActivityPause", Activity::class.java)
            declaredMethod.invoke(application, baseActivity)
        } catch (e: Exception) {

        }
    }

    @After(onActivityStop)
    open fun onActivityStopAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application
            val declaredMethod =
                application.javaClass.getDeclaredMethod("onActivityStop", Activity::class.java)
            declaredMethod.invoke(application, baseActivity)
        } catch (e: Exception) {

        }
    }

    @After(onActivityDestroy)
    open fun onActivityDestroyAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` !is BaseActivity<*, *>) {
                throw IllegalStateException("joinPoint is not BaseActivity")
            }
            val baseActivity = joinPoint.`this` as BaseActivity<*, *>
            val application = baseActivity.application
            val declaredMethod =
                application.javaClass.getDeclaredMethod("onActivityDestroy", Activity::class.java)
            declaredMethod.invoke(application, baseActivity)
        } catch (e: Exception) {

        }
    }
}