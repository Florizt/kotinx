package com.florizt.base_mvvm_lib.aop.pointcut

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
object ActivityLifeCycle {
    const val onActivityCreate: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onCreate(android.os.Bundle))"
    const val onActivityNewIntent: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onNewIntent(android.content.Intent))"
    const val onActivitySaveInstanceState: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onSaveInstanceState(android.os.Bundle))"
    const val onActivityResult: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onActivityResult(Int,Int,android.content.Intent))"
    const val onActivityStart: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onStart())"
    const val onActivityResume: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onResume())"
    const val onActivityPause: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onPause())"
    const val onActivityStop: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onStop())"
    const val onActivityDestroy: String =
        "execution(* com.florizt.base_mvvm_lib.base.ui.BaseActivity.onDestroy())"
}