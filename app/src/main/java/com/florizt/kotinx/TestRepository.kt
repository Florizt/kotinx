package com.florizt.kotinx

import com.florizt.base_mvvm_lib.base.repository.datasource.remote.Result

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
class TestRepository(private val testModel: TestModel) {
    suspend fun getTest(): Result<Int> = testModel.getName()
}