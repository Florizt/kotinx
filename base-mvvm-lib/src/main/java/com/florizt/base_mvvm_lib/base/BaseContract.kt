package com.florizt.base_mvvm_lib.base

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.florizt.base_mvvm_lib.R


/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
interface BaseContract {

    interface IView {
        fun initLayoutId(): Int
        fun initVariableId(): Int
        fun initParam() {}
        fun initView(savedInstanceState: Bundle?)
        fun initData()
        fun initViewObservable()
    }

    interface IWindow {
        /**
         * 是否拦截返回键
         */
        fun interceptKeyBack(): Boolean = false

        /**
         * 拦截后，默认操作，还是返回，其他操作请重写方法
         */
        fun defaultOpera(activity: Activity, keyCode: Int, event: KeyEvent) {
            activity.onKeyDown(keyCode, event)
        }
    }

    interface IBar {
        /**
         * 是否开启沉浸式
         *
         * @return
         */
        fun immersionBarEnabled(): Boolean = true

        /**
         * 沉浸式下是否全屏
         *
         * @return
         */
        fun isFullScreen(): Boolean = false

        /**
         * 沉浸式非全屏下状态栏字体是否深色
         *
         * @return
         */
        fun statusBarDarkFont(): Boolean = true

        /**
         * 是否fitsSystemWindows
         *
         * @return
         */
        fun fitsSystemWindows(): Boolean = true

        /**
         * 沉浸式非全屏下状态栏背景颜色
         *
         * @return
         */
        fun statusBarColor(): Int = R.color.white

        /**
         * 沉浸式非全屏下底部导航栏背景颜色
         *
         * @return
         */
        fun navigationBarColor(): Int = R.color.white

        /**
         * 解决EditText与软键盘冲突
         *
         * @return true：EditText跟随软键盘弹起，false反之
         */
        fun keyboardEnable(): Boolean = true

        /**
         * 软键盘模式
         *
         * @return
         */
        fun keyboardMode(): Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

        fun onKeyboardChange(isPopup: Boolean, keyboardHeight: Int) {}
    }

    interface IViewModel : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
        }
    }
}