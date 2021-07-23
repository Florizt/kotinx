package com.florizt.base_mvvm_lib.aop

import com.florizt.base_mvvm_lib.aop.pointcut.Permission.PermissionCheck
import com.florizt.base_mvvm_lib.aop.pointcut.Permission.onPermissionCheck
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.reflect.MethodSignature


/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
open class PermissionAspect {

    @Around(onPermissionCheck)
    open fun aroundJoinAspectPermissionCheck(joinPoint: ProceedingJoinPoint) {
        val methodSignature =
            joinPoint.signature as MethodSignature
        val permissionCheck = methodSignature.method.getAnnotation(PermissionCheck::class.java)
        permissionCheck?.let {
            if (it.permissions.size > 0) {

            }
        }
    }
}