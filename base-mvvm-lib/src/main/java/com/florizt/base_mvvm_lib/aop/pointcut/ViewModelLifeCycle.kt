package com.florizt.base_mvvm_lib.aop.pointcut

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
/**
 * 对ViewModel的生命周期进行切面
 */
object ViewModelLifeCycle {
    const val onViewModelCreate: String =
        "execution(* com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel.onCreate())"
    const val onViewModelStart: String =
        "execution(* com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel.onStart())"
    const val onViewModelResume: String =
        "execution(* com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel.onResume())"
    const val onViewModelPause: String =
        "execution(* com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel.onPause())"
    const val onViewModelStop: String =
        "execution(* com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel.onStop())"
    const val onViewModelDestroy: String =
        "execution(* com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel.onDestroy())"
    const val onViewModelAny: String =
        "execution(* com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel.onAny(androidx.lifecycle.LifecycleOwner,androidx.lifecycle.Lifecycle.Event))"
}