package com.florizt.base_mvvm_lib.base.repository.datasource.remote

import android.net.ParseException
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLHandshakeException

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
object DealException {
    fun handlerException(e: Exception): ResultException {
        val ex: ResultException
        if (e is ResultException) {
            ex = e
        } else if (e is HttpException) {
            ex = when (e.code()) {
                HttpResultCode.UNAUTHORIZED,
                HttpResultCode.FORBIDDEN,
                    //权限错误，需要实现
                HttpResultCode.NOT_FOUND -> ResultException(
                    e.code().toString(),
                    "网络错误"
                )
                HttpResultCode.REQUEST_TIMEOUT,
                HttpResultCode.GATEWAY_TIMEOUT -> ResultException(
                    e.code().toString(),
                    "网络连接超时"
                )
                HttpResultCode.INTERNAL_SERVER_ERROR,
                HttpResultCode.BAD_GATEWAY,
                HttpResultCode.SERVICE_UNAVAILABLE -> ResultException(
                    e.code().toString(),
                    "服务器错误"
                )
                else -> ResultException(
                    e.code().toString(),
                    "网络错误"
                )
            }
        } else if (e is SocketException) {
            ex =
                ResultException(
                    HttpResultCode.REQUEST_TIMEOUT.toString(),
                    "网络连接错误，请重试"
                )
        } else if (e is SocketTimeoutException) {
            ex =
                ResultException(
                    HttpResultCode.REQUEST_TIMEOUT.toString(),
                    "网络连接超时"
                )
        } else if (e is JsonParseException
            || e is JSONException
            || e is ParseException
        ) {
            ex =
                ResultException(
                    HttpResultCode.PARSE_ERROR,
                    "解析错误"
                )
        } else if (e is SSLHandshakeException) {
            ex =
                ResultException(
                    HttpResultCode.SSL_ERROR,
                    "证书验证失败"
                )
            return ex
        } else if (e is UnknownHostException) {
            ex =
                ResultException(
                    HttpResultCode.UNKNOW_HOST,
                    "网络错误，请切换网络重试"
                )
            return ex
        } else if (e is UnknownServiceException) {
            ex =
                ResultException(
                    HttpResultCode.UNKNOW_HOST,
                    "网络错误，请切换网络重试"
                )
        } else if (e is NumberFormatException) {
            ex =
                ResultException(
                    HttpResultCode.UNKNOW_HOST,
                    "数字格式化异常"
                )
        } else {
            ex =
                ResultException(
                    HttpResultCode.UNKNOWN,
                    "未知错误"
                )
        }
        return ex
    }
}