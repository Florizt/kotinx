package com.florizt.base_mvvm_lib.base.ui

import android.app.Activity
import androidx.fragment.app.Fragment
import java.lang.ref.SoftReference
import java.util.*


/**
 * Created by wuwei
 * 2021/7/23
 * 佛祖保佑       永无BUG
 */
/**
 * Activity堆栈管理类，包括应用进入前后台管理
 * @property appCount Int
 * @property runInBackground Boolean
 * @property activityStack SoftReference<Stack<Activity>>
 * @property fragmentStack SoftReference<Stack<Fragment>>
 */
class AppStackManager private constructor() {
    companion object {
        val instance: AppStackManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppStackManager()
        }
    }

    private var appCount: Int = 0
    private var runInBackground: Boolean = true

    private val activityStack: SoftReference<Stack<Activity>> = SoftReference(Stack<Activity>())
    private val fragmentStack: SoftReference<Stack<Fragment>> = SoftReference(Stack<Fragment>())

    fun getAppCount() = appCount

    fun setAppCount(count: Int) {
        appCount = count
    }

    fun getRunInBackground() = runInBackground

    fun setRunInBackground(background: Boolean) {
        runInBackground = background
    }

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        activityStack.get()?.also { it.add(activity) }
    }

    /**
     * 移除指定的Activity
     */
    fun removeActivity(activity: Activity) {
        activityStack.get()?.also { it.remove(activity) }
    }


    /**
     * 是否有activity
     */
    fun hasActivity(): Boolean {
        return activityStack.get()?.let { if (it.isEmpty()) false else true } ?: false
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return activityStack.get()?.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        activityStack.get()?.let { finishActivity(it.lastElement()) }
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        if (!activity.isFinishing) {
            activity.finish()
        }
        removeActivity(activity)
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        activityStack.get()?.let {
            for (activity in it) {
                if (activity.javaClass == cls) {
                    finishActivity(activity)
                    break
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        activityStack.get()?.let {
            it.forEach {
                it?.let { finishActivity(it) }
            }
            it.clear()
        }
    }

    /**
     * 获取指定的Activity
     */
    fun getActivity(cls: Class<*>): Activity? {
        activityStack.get()?.let {
            for (activity in it) {
                if (activity.javaClass == cls) {
                    return activity
                }
            }
        }
        return null
    }


    /**
     * 添加Fragment到堆栈
     */
    fun addFragment(fragment: Fragment) {
        fragmentStack.get()?.also { it.add(fragment) }
    }

    /**
     * 移除指定的Fragment
     */
    fun removeFragment(fragment: Fragment) {
        fragmentStack.get()?.also { it.remove(fragment) }
    }


    /**
     * 是否有Fragment
     */
    fun hasFragment(): Boolean {
        return fragmentStack.get()?.let { if (it.isEmpty()) false else true } ?: false
    }

    /**
     * 获取当前Fragment（堆栈中最后一个压入的）
     */
    fun currentFragment(): Fragment? {
        return fragmentStack.get()?.lastElement()
    }


    /**
     * 退出应用程序
     */
    fun appExit() {
        try {
            finishAllActivity()
            // 杀死该应用进程
//            android.os.Process.killProcess(android.os.Process.myPid());
//            调用 System.exit(n) 实际上等效于调用：
//            Runtime.getRuntime().exit(n)
//            finish()是Activity的类方法，仅仅针对Activity，当调用finish()时，只是将活动推向后台，并没有立即释放内存，活动的资源并没有被清理；
//            当调用System.exit(0)时，退出当前Activity并释放资源（内存），但是该方法不可以结束整个App如有多个Activty或者有其他组件service等不会结束。
//            其实android的机制决定了用户无法完全退出应用，当你的application最长时间没有被用过的时候，android自身会决定将application关闭了。
//            System.exit(0);
        } catch (e: Exception) {
            activityStack.get()?.also { it.clear() }
            e.printStackTrace()
        }
    }
}