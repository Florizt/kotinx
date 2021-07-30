package com.florizt.base_mvvm_lib.base.viewmodel

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
/**
 * 用于列表等Item绑定的ViewModel，持有BaseViewModel
 * @param VM : BaseViewModel
 * @property viewModel VM
 * @constructor
 */
open class BaseItemViewModel<VM : BaseViewModel>(val viewModel: VM)