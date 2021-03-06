package com.florizt.kotinx

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.florizt.base_mvvm_lib.base.ui.BaseActivity
import com.florizt.kotinx.databinding.ActivityMainBinding

/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
class TestActivity : BaseActivity<ActivityMainBinding, TestViewModel>() {

    override fun initLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun initViewObservable() {
        viewModel.test.observed(this, Observer {
            println(">>>>>>>>>adapterTest=222======：${it}")
        })
    }
}