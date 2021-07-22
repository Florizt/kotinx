package com.florizt.base_mvvm_lib.aop

import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityStart
import com.florizt.base_mvvm_lib.base.ui.BaseActivity
import com.gyf.barlibrary.BarHide
import com.gyf.barlibrary.ImmersionBar
import com.gyf.barlibrary.OnKeyboardListener
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
@Aspect
open class BarAspect {
    @After(onActivityStart)
    open fun initBarAspect(joinPoint: JoinPoint) {
        if (joinPoint.`this` is BaseActivity<*, *>) {
            initImmersionBar(joinPoint.`this` as BaseActivity<*, *>)
        }
    }

    private fun initImmersionBar(activity: BaseActivity<*, *>) {
        if (activity.immersionBarEnabled()) {
            if (activity.isFullScreen()) {
                ImmersionBar.with(activity)
                    .hideBar(BarHide.FLAG_HIDE_BAR)
                    .init()
            } else {
                ImmersionBar.with(activity)
                    .fitsSystemWindows(activity.fitsSystemWindows())
                    .statusBarDarkFont(activity.statusBarDarkFont(), 0.2f)
                    .statusBarColor(activity.statusBarColor())
                    .navigationBarColor(activity.navigationBarColor())
                    .keyboardEnable(activity.keyboardEnable())
                    .keyboardMode(activity.keyboardMode())
                    .setOnKeyboardListener(OnKeyboardListener { isPopup, keyboardHeight ->
                        activity.onKeyboardChange(isPopup, keyboardHeight)
                    })
                    .init()
            }
        }
    }
}