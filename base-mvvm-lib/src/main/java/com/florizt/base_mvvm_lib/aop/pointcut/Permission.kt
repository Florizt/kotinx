package com.florizt.base_mvvm_lib.aop.pointcut

/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
object Permission {
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class PermissionCheck(val permissions: Array<String>)

    const val onPermissionCheck: String =
        "execution(@com.florizt.base_mvvm_lib.aop.pointcut.Permission.PermissionCheck * *(..))"
}