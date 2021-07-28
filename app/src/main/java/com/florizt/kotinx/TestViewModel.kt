package com.florizt.kotinx

import androidx.databinding.ObservableInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.florizt.base_mvvm_lib.base.repository.datasource.MessageEvent
import com.florizt.base_mvvm_lib.base.repository.datasource.SingleLiveData
import com.florizt.base_mvvm_lib.base.repository.datasource.remote.Result
import com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel
import com.florizt.base_mvvm_lib.ext.IDLE
import com.florizt.base_mvvm_lib.ext.launchUI
import com.florizt.base_mvvm_lib.ext.launchWithIO
import org.greenrobot.eventbus.EventBus

/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
class TestViewModel(private val testRepository: TestRepository) : BaseViewModel() {

    override fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
        println("onAny $owner---$event")
    }

    val test: SingleLiveData<String> = SingleLiveData("xxx")

    var status: ObservableInt = ObservableInt(IDLE)


    override fun onCreate() {
        super.onCreate()
        println("onCreate")
        launchUI {
            launchWithIO {
                val age = testRepository.getAge()
                val name = testRepository.getName()
                if (age is Result.Success) {
                    println("===${age.data}")
                } else if (age is Result.Failed) {

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun adapterTest() = {
        println(">>>>>>>>>aspectj=111======：${status.get()}")
        EventBus.getDefault().post(MessageEvent(1, 0))
        if (status.get() == 4) {
            status.set(0)
        } else {
            status.set(status.get() + 1)
        }
    }

    fun reLoadData() = {

    }

    override fun onMessageEvent(event: MessageEvent?) {
        println("MessageEvent>>>>>>=====$event==========")
    }

}