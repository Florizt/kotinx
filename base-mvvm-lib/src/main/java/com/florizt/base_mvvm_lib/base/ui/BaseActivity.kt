package com.florizt.base_mvvm_lib.base.ui

import android.content.Intent
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
/**
 * 轻量的Activity基类
 * @param V : ViewDataBinding
 * @param VM : BaseViewModel
 * @property activity AppCompatActivity
 * @property binding V
 * @property viewModel VM
 */
abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel> :
    AppCompatActivity(), BaseContract.IView, BaseContract.IWindow, BaseContract.IBar {

    protected val activity: AppCompatActivity = this@BaseActivity
    protected lateinit var binding: V
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam()
        initDatabindingAndViewModel()
        initView(savedInstanceState)
        initData()
        registorUIChangeLiveDataCallBack()
        initViewObservable()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && interceptKeyBack()) {
            defaultOpera(activity, keyCode, event)
            false
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun initDatabindingAndViewModel() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity), initLayoutId(), null, false
        )
        setContentView(binding.root)

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

        // databinding绑定viewModel，不然xml用不了viewModel
        binding.setVariable(initVariableId(), viewModel)

        // databinding绑定LiveData，不然xml收不到数据改变通知
        // 也可以直接使用DataBinding的ObservableField
        // 注意：LiveData如果被observed，那么xml和observed都会收到通知，所以LiveData和ObservableField需要区分场景使用
        binding.lifecycleOwner = this

        // viewModel绑定lifecycle，不然viewModel没有Activity生命周期
        lifecycle.addObserver(viewModel)
    }

    private fun registorUIChangeLiveDataCallBack() {
        viewModel.uc.getShowDialogEvent()?.observed(this, Observer<Nothing?> {

        })

        viewModel.uc.getDismissDialogEvent()?.observed(this, Observer<Nothing?> {

        })

        viewModel.uc.getHideSoftKeyBoardEvent()?.observed(this, Observer<Nothing?> {
            hideSoftKeyBoard()
        })

        viewModel.uc.getBackEvent()?.observed(this, Observer<Nothing?> {

        })
    }
}