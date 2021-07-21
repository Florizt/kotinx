package com.florizt.base_mvvm_lib.base

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
interface BaseContract {

    interface IView {
        fun initLayoutId(): Int
        fun initVariableId(): Int
        fun initParam()
        fun initView(savedInstanceState: Bundle?)
        fun initData()
        fun initViewObservable()
    }

    interface IViewModel : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?)

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate()

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy()

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart()

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop()

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume()

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause()
    }
}