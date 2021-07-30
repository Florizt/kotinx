package com.florizt.kotinx

import com.florizt.base_mvvm_lib.base.repository.datasource.entity.BaseResponse
import com.florizt.base_mvvm_lib.base.repository.datasource.locate.L_GET
import com.florizt.base_mvvm_lib.base.repository.datasource.locate.L_POST
import com.florizt.base_mvvm_lib.base.repository.datasource.locate.LocalType

/**
 * Created by wuwei
 * 2021/7/21
 * 佛祖保佑       永无BUG
 */
interface ITestLocalService {
    @L_POST(type = LocalType.SP, key = arrayOf("name"))
    suspend fun setName(name: String): BaseResponse<Unit>

    @L_GET(type = LocalType.SP, key = arrayOf("name"))
    suspend fun getName(): BaseResponse<String>
}