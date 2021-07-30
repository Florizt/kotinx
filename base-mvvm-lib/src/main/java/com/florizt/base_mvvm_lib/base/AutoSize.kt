package com.florizt.base_mvvm_lib.base

import me.jessyan.autosize.AutoSize

/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
/**
 * 在Application中的onCreate方法使用，配置应用适配尺寸
 * 在[com.florizt.base_mvvm_lib.aop.ApplicationAspect]中切面处理
 * 在[BaseApp]中使用[AutoSize]进行自动适配
 * @property designWidthInDp Int
 * @property designHeightInDp Int
 * @constructor
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoSize(val designWidthInDp: Int, val designHeightInDp: Int)