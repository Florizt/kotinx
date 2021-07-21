package com.florizt.base_mvvm_lib.base.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.florizt.base_mvvm_lib.base.BaseContract
import com.florizt.base_mvvm_lib.base.repository.datasource.SingleLiveData


/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
abstract class BaseViewModel : ViewModel(), BaseContract.IViewModel {

    val uc: UIChangeLiveData<Any> = UIChangeLiveData()

    inner class UIChangeLiveData<T> : SingleLiveData<T>() {
        private var showDialogEvent: SingleLiveData<Nothing?>? = null
        private var dismissDialogEvent: SingleLiveData<Nothing?>? = null
        private var hideSoftKeyBoard: SingleLiveData<Nothing?>? = null
        private var back: SingleLiveData<Nothing?>? = null

        fun getShowDialogEvent(): SingleLiveData<Nothing?>? {
            return createLiveData<Nothing?>(showDialogEvent).also({ showDialogEvent = it })
        }

        fun getDismissDialogEvent(): SingleLiveData<Nothing?>? {
            return createLiveData<Nothing?>(dismissDialogEvent).also({ dismissDialogEvent = it })
        }

        fun getHideSoftKeyBoardEvent(): SingleLiveData<Nothing?>? {
            return createLiveData<Nothing?>(hideSoftKeyBoard).also({ hideSoftKeyBoard = it })
        }

        fun getBackEvent(): SingleLiveData<Nothing?>? {
            return createLiveData<Nothing?>(back).also({ back = it })
        }

        private fun <T> createLiveData(liveData: SingleLiveData<T?>?): SingleLiveData<T?>? {
            var liveData: SingleLiveData<T?>? = liveData
            if (liveData == null) {
                liveData =
                    SingleLiveData()
            }
            return liveData
        }
    }

    override fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }
}