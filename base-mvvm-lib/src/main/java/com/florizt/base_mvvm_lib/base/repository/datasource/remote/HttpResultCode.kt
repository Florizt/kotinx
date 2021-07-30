package com.florizt.base_mvvm_lib.base.repository.datasource.remote

/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */
/**
 * http常见错误码
 */
object HttpResultCode {

    //对应HTTP的状态码
    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val REQUEST_TIMEOUT = 408
    const val INTERNAL_SERVER_ERROR = 500
    const val BAD_GATEWAY = 502
    const val SERVICE_UNAVAILABLE = 503
    const val GATEWAY_TIMEOUT = 504

    // 自定义HTTP错误码
    const val RESULT_NORMAL = "0"//正常
    const val UNKNOWN = "1000"
    const val PARSE_ERROR = "1001"
    const val UNKNOW_HOST = "1002"
    const val SSL_ERROR = "1003"
}