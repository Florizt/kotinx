package com.florizt.base_mvvm_lib.base

import android.content.Context
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
class BaseApp private constructor() {
    private var context: Context? = null

    companion object {
        val instance: BaseApp by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BaseApp()
        }
    }

    fun getContext() = context

    fun init(ctx: Context, designWidthInDp: Int, designHeightInDp: Int) {
        context = ctx.applicationContext

        AutoSize.initCompatMultiProcess(context);
        AutoSizeConfig.getInstance().setDesignWidthInDp(designWidthInDp);
        AutoSizeConfig.getInstance().setDesignHeightInDp(designHeightInDp);
    }
}