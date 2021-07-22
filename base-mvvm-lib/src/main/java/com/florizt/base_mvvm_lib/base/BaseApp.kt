package com.florizt.base_mvvm_lib.base

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */
class BaseApp {
    companion object {
        var test: Int? = 100
    }

    fun setTest(t: Int) {
        test = t
    }

    fun getTest() = test
}