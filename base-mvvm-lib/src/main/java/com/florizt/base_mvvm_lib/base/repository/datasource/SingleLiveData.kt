package com.florizt.base_mvvm_lib.base.repository.datasource

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
open class SingleLiveData<T> : MutableLiveData<T>() {
    private val TAG = "SingleLiveEvent"

    private val pending: AtomicBoolean = AtomicBoolean(false)

    @MainThread
    fun observed(owner: LifecycleOwner?, observer: Observer<in T?>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }
        // Observe the internal MutableLiveData
        super.observe(owner!!, object : Observer<T> {
            override fun onChanged(t: T) {
                if (pending.compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            }
        })
    }

    /**
     * 只允许在主线程调用，如果在其他线程调用会报错：This method must be called from the main thread
     */
    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }
}