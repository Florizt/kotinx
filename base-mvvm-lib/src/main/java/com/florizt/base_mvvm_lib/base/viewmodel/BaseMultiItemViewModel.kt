package com.florizt.base_mvvm_lib.base.viewmodel


/**
 * Created by wuwei
 * 2021/7/21
 * 佛祖保佑       永无BUG
 */
class BaseMultiItemViewModel<VM : BaseViewModel>(viewModel: VM) :
    BaseItemViewModel<VM>(viewModel) {

    var multiType: Any? = null
        get() = field
        set(value) {
            field = value
        }
}