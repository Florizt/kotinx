package com.florizt.kotinx

import com.florizt.base_mvvm_lib.base.repository.datasource.remote.BaseHttpResult
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
interface ITestRemoteService {
    @GET("get/remote")
    suspend fun getTest(@Query("name") name: String): BaseHttpResult<Int>
}