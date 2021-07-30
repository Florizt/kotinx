package com.florizt.kotinx

import com.florizt.base_mvvm_lib.base.repository.datasource.entity.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
interface ITestRemoteService {
    @GET("get/remote")
    suspend fun getAge(@Query("uid") uid: String): BaseResponse<Int>
}