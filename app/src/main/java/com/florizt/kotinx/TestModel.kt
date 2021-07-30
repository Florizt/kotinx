package com.florizt.kotinx

import com.florizt.base_mvvm_lib.base.repository.datasource.entity.Result
import com.florizt.base_mvvm_lib.base.repository.model.BaseModel

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
class TestModel(private val remoteService: ITestRemoteService,private val localService: ITestLocalService) : BaseModel() {

    suspend fun getAge(): Result<Int> = callRequest {
        handleHttpResponse(remoteService.getAge("aaa"))
    }

    suspend fun setName(name: String): Result<Unit> = callRequest {
        handleLocalResponse(localService.setName(name))
    }

    suspend fun getName(): Result<String> = callRequest {
        handleLocalResponse(localService.getName())
    }
}