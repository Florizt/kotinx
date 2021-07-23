package com.florizt.kotinx

import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityStart
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
@Aspect
open class StatisticAspect {
    @After(onActivityStart)
    open fun statisticAspect(joinPoint: JoinPoint){
        println("----statisticAspect------------")
    }
}