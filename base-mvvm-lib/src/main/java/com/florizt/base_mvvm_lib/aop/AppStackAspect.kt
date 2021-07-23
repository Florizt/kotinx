package com.florizt.base_mvvm_lib.aop

import android.app.Activity
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityCreate
import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityPause
import com.florizt.base_mvvm_lib.base.ui.AppStackManager
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect

/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
@Aspect
open class AppStackAspect {
    @After(onActivityCreate)
    open fun addActivityAspect(joinPoint: JoinPoint) {
        AppStackManager.instance.addActivity(joinPoint.`this` as Activity)
    }

    @After(onActivityPause)
    open fun removeActivityAspect(joinPoint: JoinPoint) {
        if (joinPoint.`this` is Activity) {
            val activity = joinPoint.`this` as Activity
            if (activity.isFinishing()) {
                AppStackManager.instance.removeActivity(activity)
            }
        }
    }
}