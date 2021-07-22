package com.florizt.base_mvvm_lib.aop

import com.florizt.base_mvvm_lib.aop.pointcut.ViewModelLifeCycle.onViewModelCreate
import com.florizt.base_mvvm_lib.aop.pointcut.ViewModelLifeCycle.onViewModelDestroy
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.greenrobot.eventbus.EventBus

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
@Aspect
open class EventBusAspect {
    @After(onViewModelCreate)
    open fun initEventBusAspect(joinPoint: JoinPoint) {
        if (!EventBus.getDefault().isRegistered(joinPoint.`this`)) {
            EventBus.getDefault().register(joinPoint.`this`);
        }
    }

    @After(onViewModelDestroy)
    open fun destroyEventBusAspect(joinPoint: JoinPoint) {
        EventBus.getDefault().unregister(joinPoint.`this`);
    }
}