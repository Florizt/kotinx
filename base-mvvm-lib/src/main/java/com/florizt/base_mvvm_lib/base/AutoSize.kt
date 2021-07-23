package com.florizt.base_mvvm_lib.base

/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoSize(val designWidthInDp: Int, val designHeightInDp: Int)