package com.florizt.kotinx

import com.florizt.base_mvvm_lib.base.repository.datasource.entity.Result

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
class TestRepository(private val testModel: TestModel) {
    suspend fun getAge(): Result<Int> = testModel.getAge()
    suspend fun getName(): Result<String> = testModel.getName()
    suspend fun setName(name: String): Result<Unit> = testModel.setName(name)
}