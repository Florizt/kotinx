package com.florizt.base_mvvm_lib.base.repository.model

import com.florizt.base_mvvm_lib.base.repository.datasource.entity.BaseResponse
import com.florizt.base_mvvm_lib.base.repository.datasource.entity.Result
import com.florizt.base_mvvm_lib.base.repository.datasource.entity.ResultException
import com.florizt.base_mvvm_lib.base.repository.datasource.locate.LocalResultCode
import com.florizt.base_mvvm_lib.base.repository.datasource.remote.DispatchException
import com.florizt.base_mvvm_lib.base.repository.datasource.remote.HttpResultCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
/**
 * Model层基类，所有model都必须继承BaseModel
 */
open class BaseModel {
    /**
     * 进行网络请求或者本地数据请求
     * @param call SuspendFunction0<Result<T>>
     * @return Result<T> 无论是网络请求还是本地数据请求，返回结果都是[Result]
     */
    suspend fun <T : Any> callRequest(call: suspend () -> Result<T>): Result<T> {
        return try {
            call()
        } catch (e: Exception) {
            //这里统一处理异常
            e.printStackTrace()
            Result.Failed(DispatchException.handlerException(e))
        }
    }

    /**
     * 网络请求
     * @param response BaseResponse<T>
     * @param successBlock [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>?
     * @param errorBlock [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>?
     * @return Result<T>
     */
    suspend fun <T : Any> handleHttpResponse(
        response: BaseResponse<T>,
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

    /**
     * 本地数据请求
     * @param response T
     * @param successBlock [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>?
     * @return Result<T>
     */
    suspend fun <T : Any> handleLocalResponse(
        response: BaseResponse<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): Result<T> {
        return coroutineScope {
            if (response.code.toInt() == LocalResultCode.RESULT_SUCCESS.toInt()) {
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