package com.florizt.kotinx

import com.florizt.base_mvvm_lib.base.repository.datasource.remote.Result
import com.florizt.base_mvvm_lib.base.repository.model.BaseModel

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
class TestModel(private val remoteService: ITestRemoteService,private val localService: ITestLocalService) : BaseModel() {

    suspend fun getName(): Result<Int> = callRequest {
        println("----$remoteService")
        handleResponse(remoteService.getTest("aaa"))
    }
}