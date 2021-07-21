package com.florizt.base_mvvm_lib.base.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.florizt.base_mvvm_lib.base.BaseContract
import com.florizt.base_mvvm_lib.base.viewmodel.BaseViewModel
import com.florizt.base_mvvm_lib.ext.hideSoftKeyBoard
import org.koin.android.viewmodel.ext.android.getViewModel
import kotlin.reflect.KClass


/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel> :
    AppCompatActivity(), BaseContract.IView {

    protected val activity: AppCompatActivity? = this@BaseActivity
    protected var binding: V? = null
    protected var viewModel: VM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam()
        initDatabindingAndViewModel()
        initView(savedInstanceState)
        initData()
        registorUIChangeLiveDataCallBack()
        initViewObservable()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return false
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    private fun initDatabindingAndViewModel() {
        binding = DataBindingUtil.inflate<V>(
            LayoutInflater.from(activity), initLayoutId(), null, false
        )
        setContentView((binding as V).root)

        if (viewModel == null) {
            // 传统写法：
            /* var clazz: Class<VM>? = null
             val genericSuperclass = javaClass.genericSuperclass;
             if (genericSuperclass is ParameterizedType) {
                 clazz = genericSuperclass?.actualTypeArguments[1] as Class<VM>?
             }
             if (clazz == null) {
                 clazz = BaseViewModel::class.java as Class<VM>?
             }
             viewModel = clazz?.let { ViewModelProviders.of(this).get(it) }*/

            // koin写法，需要 kotlin-reflect 和 koin
            val clazzs =
                javaClass.kotlin.supertypes[0].arguments[1].type!!.classifier!! as KClass<VM>
            viewModel = getViewModel(clazzs)
        }

        // databinding绑定viewModel，不然xml用不了viewModel
        viewModel?.let { binding?.setVariable(initVariableId(), it) }

        // databinding绑定livedata，不然xml收不到数据改变通知，也可以直接使用DataBinding的ObservableField
        binding?.lifecycleOwner = this

        // viewModel绑定lifecycle，不然viewModel没有Activity生命周期
        viewModel?.let { lifecycle.addObserver(it) }
    }

    private fun registorUIChangeLiveDataCallBack() {
        viewModel?.uc?.getShowDialogEvent()?.observed(this, Observer<Nothing?> {

        })

        viewModel?.uc?.getDismissDialogEvent()?.observed(this, Observer<Nothing?> {

        })

        viewModel?.uc?.getHideSoftKeyBoardEvent()?.observed(this, Observer<Nothing?> {
            hideSoftKeyBoard()
        })

        viewModel?.uc?.getBackEvent()?.observed(this, Observer<Nothing?> {

        })
    }
}