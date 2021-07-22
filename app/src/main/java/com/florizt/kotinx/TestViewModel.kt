package com.florizt.kotinx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.florizt.base_mvvm_lib.base.repository.datasource.MessageEvent
import com.florizt.base_mvvm_lib.base.repository.datasource.SingleLiveData
import com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel
import com.florizt.base_mvvm_lib.ext.launchUI
import com.florizt.base_mvvm_lib.ext.launchWithUI
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


    override fun onCreate() {
        super.onCreate()
        println("onCreate")
        launchUI {
            //            launchWithIO { testRepository.getTest() }

            launchWithUI { }

            val testItemViewModel = TestItemViewModel(this@TestViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun adapterTest() = {
        println("====aspectj=111======")
        EventBus.getDefault().post(MessageEvent(1, 0))
    }

    override fun onMessageEvent(event: MessageEvent?) {
        println("MessageEvent>>>>>>=====$event==========")
    }

}