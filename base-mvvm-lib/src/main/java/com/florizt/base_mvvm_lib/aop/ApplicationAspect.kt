package com.florizt.base_mvvm_lib.aop

import android.app.Application
import com.florizt.base_mvvm_lib.base.AutoSize
import com.florizt.base_mvvm_lib.base.BaseApp
import com.florizt.base_mvvm_lib.base.CrashHandler
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect

/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
/**
 * 对需要在Application中初始化框架的操作，进行切面处理
 * 包括 [BaseApp] 中的全局Context和[AutoSize]适配
 * 包括 [CrashHandler] 中的全局异常捕获
 */
@Aspect
open class ApplicationAspect {
    @After("execution(* android.app.Application.onCreate())")
    open fun applicationInitAspect(joinPoint: JoinPoint) {
        try {
            if (joinPoint.`this` is Application) {
                val application = joinPoint.`this` as Application
                val declaredMethod =
                    application.javaClass.getDeclaredMethod("onCreate")

                var designWidthInDp: Int = 360
                var designHeightInDp: Int = 640
                for (anno in declaredMethod.annotations) {
                    if (anno is AutoSize) {
                        designWidthInDp = anno.designWidthInDp
                        designHeightInDp = anno.designHeightInDp
                        break
                    }
                }

                BaseApp.instance.init(
                    application.applicationContext,
                    designWidthInDp,
                    designHeightInDp
                )
                CrashHandler.instance.init(application.applicationContext)
            }
        } catch (e: Exception) {

        }
    }
}