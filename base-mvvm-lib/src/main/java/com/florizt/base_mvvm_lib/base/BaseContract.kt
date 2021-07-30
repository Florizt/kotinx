package com.florizt.base_mvvm_lib.base

import android.app.Activity
import android.content.Intent
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
/**
 * 框架每一层的接口
 */
interface BaseContract {

    /**
     * 用于Application实现--开放生命周期、前后台、异常捕获
     */
    interface IApplication {
        fun onActivityCreate(activity: Activity, savedInstanceState: Bundle?) {}
        fun onActivityNewIntent(activity: Activity, intent: Intent?) {}
        fun onActivitySaveInstanceState(activity: Activity, savedInstanceState: Bundle?) {}
        fun onActivityResult(
            activity: Activity,
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ) {
        }

        fun onActivityStart(activity: Activity) {}
        fun onActivityResume(activity: Activity) {}
        fun onActivityPause(activity: Activity) {}
        fun onActivityStop(activity: Activity) {}
        fun onActivityDestroy(activity: Activity) {}
        fun toBackground(activity: Activity) {}
        fun toForeground(activity: Activity) {}
        fun crashOpera(ex: Throwable) {}
    }

    /**
     * 用于BaseActivity实现--布局层
     */
    interface IView {
        fun initLayoutId(): Int
        fun initVariableId(): Int
        fun initParam() {}
        fun initView(savedInstanceState: Bundle?)
        fun initData()
        fun initViewObservable()
    }

    /**
     * 用于BaseActivity实现--窗口层
     */
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

    /**
     * 用于BaseActivity实现--状态栏、导航栏、软键盘等系统层
     */
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
         * 是否fitsSystemWindows
         *
         * @return
         */
        fun fitsSystemWindows(): Boolean = true

        /**
         * 沉浸式非全屏下状态栏字体是否深色
         *
         * @return
         */
        fun statusBarDarkFont(): Boolean = true


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

        /**
         * 软键盘监听
         * @param isPopup Boolean 是否弹起
         * @param keyboardHeight Int 弹起高度
         */
        fun onKeyboardChange(isPopup: Boolean, keyboardHeight: Int) {}
    }

    /**
     * 用于BaseViewModel实现--生命周期
     */
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