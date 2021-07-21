package com.florizt.base_mvvm_lib.base.repository.model

import com.florizt.base_mvvm_lib.base.repository.datasource.remote.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
open class BaseModel {
    suspend fun <T : Any> callRequest(call: suspend () -> Result<T>): Result<T> {
        return try {
            call()
        } catch (e: Exception) {
            //这里统一处理异常
            e.printStackTrace()
            Result.Failed(DealException.handlerException(e))
        }
    }

    suspend fun <T : Any> handleResponse(
        response: BaseHttpResult<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): Result<T> {
        return coroutineScope {
            if (response.code.toInt() == HttpResultCode.RESULT_NORMAL.toInt()) {
                successBlock?.let { it() }
                Result.Success(response.data)
            } else {
                errorBlock?.let { it() }
                Result.Failed(
                    ResultException(
                        response.code,
                        response.msg
                    )
                )
            }
        }
    }
}