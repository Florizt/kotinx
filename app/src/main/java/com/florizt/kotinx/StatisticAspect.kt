package com.florizt.kotinx

import com.florizt.base_mvvm_lib.aop.pointcut.ActivityLifeCycle.onActivityCreate
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
@Aspect
open class StatisticAspect {
    @Before(onActivityCreate)
    open fun statisticAspect(joinPoint: JoinPoint){
        // ...
    }
}