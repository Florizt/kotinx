package com.florizt.kotinx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.florizt.base_mvvm_lib.base.repository.datasource.SingleLiveData
import com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel
import com.florizt.base_mvvm_lib.ext.launchUI
import com.florizt.base_mvvm_lib.ext.launchWithUI
import kotlinx.coroutines.delay

/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
class TestViewModel(private val testRepository: TestRepository) : BaseViewModel() {

    override fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
        println("onAny $owner---$event")
    }

    val test: SingleLiveData<String> = SingleLiveData()

    override fun onCreate() {
        println("onCreate")
        launchUI {
            //            launchWithIO { testRepository.getTest() }

            launchWithUI { }

            delay(2000)
            test.value = "xxx"

            val testItemViewModel = TestItemViewModel(this@TestViewModel)
        }
    }

    fun adapterTest() = {

    }

    override fun onDestroy() {
        println("onDestroy")
    }

    override fun onStart() {
        println("onStart")
    }

    override fun onStop() {
        println("onStop")
    }

    override fun onResume() {
        println("onResume")
    }

    override fun onPause() {
        println("onPause")
    }
}