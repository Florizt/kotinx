package com.florizt.base_mvvm_lib.base.repository.datasource.locate

import android.content.Context
import com.florizt.base_mvvm_lib.base.repository.datasource.entity.BaseResponse
import com.florizt.base_mvvm_lib.ext.getDecrypt
import com.florizt.base_mvvm_lib.ext.getSharedPreferences
import com.florizt.base_mvvm_lib.ext.put
import com.florizt.base_mvvm_lib.ext.remove
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.jvm.kotlinFunction

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
/***
 * 对本地存储Service进行增强
 * @property context Context 上下文
 * @property psw String? 加解密秘钥，非必须，若传了psw，则对SP存储进行3DES加密
 * @constructor
 */
class Localfit(private var context: Context, private var psw: String?) {

    fun <S> create(service: Class<S>): S {
        if (!service.isInterface) {
            throw  IllegalArgumentException("API declarations must be interfaces.");
        }
        if (service.interfaces.size > 0) {
            throw  IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service),
            InvocationHandler { any, method, args ->
                if (method.declaringClass == Any::class.java) {
                    method.invoke(this, args)
                }

                // suspend修饰符会在参数中新增一个Continuation，所以要动态去除
                var argList: List<Any>? = null
                if (method.kotlinFunction?.isSuspend == true) {
                    val newArr = arrayOf<Any>(args.size - 1)
                    for (i in newArr.indices) {
                        newArr[i] = args[i]
                    }
                    argList = newArr.toList()
                } else {
                    argList = args.toList()
                }

                parseMethodAnnotation(context, psw, method, argList)
            }) as S
    }

    fun parseMethodAnnotation(
        context: Context,
        psw: String?,
        method: Method,
        args: List<Any>?
    ): BaseResponse<Any> {
        val annotations = method.annotations
        if (annotations.size != 1) {
            return BaseResponse(
                LocalResultCode.RESULT_FAILED,
                Any(),
                "请求失败"
            )
        }
        val annotation = annotations.get(0)

        if (annotation is L_GET) {
            val type = annotation.type
            val key = annotation.key
            if (type == LocalType.SP) {
                // suspend修饰符会将返回值类型修改为Object，所以需要用到kotlinFunction
                val response = context.getSharedPreferences()
                    .getDecrypt(key[0], psw, method.kotlinFunction?.returnType)
                return response?.let {
                    BaseResponse(
                        LocalResultCode.RESULT_SUCCESS,
                        response,
                        "请求成功"
                    )
                } ?: BaseResponse(
                    LocalResultCode.RESULT_FAILED,
                    Any(),
                    "请求失败"
                )
            }
        } else if (annotation is L_POST) {
            val type = annotation.type
            val key = annotation.key
            if (args == null || key.size != args.size) {
                return BaseResponse(
                    LocalResultCode.RESULT_FAILED,
                    Any(),
                    "请求失败"
                )
            }

            if (type == LocalType.SP) {
                var response = false
                key.forEachIndexed { index, s ->
                    response = context.getSharedPreferences().put(s, args.get(index), psw)
                }
                if (response) {
                    return BaseResponse(
                        LocalResultCode.RESULT_SUCCESS,
                        response,
                        "请求成功"
                    )
                } else {
                    return BaseResponse(
                        LocalResultCode.RESULT_FAILED,
                        Any(),
                        "请求失败"
                    )
                }
            }
        } else if (annotation is L_PUT) {
            val type = annotation.type
            val key = annotation.key
            if (args == null || key.size != args.size) {
                return BaseResponse(
                    LocalResultCode.RESULT_FAILED,
                    Any(),
                    "请求失败"
                )
            }

            if (type == LocalType.SP) {
                var response = false
                key.forEachIndexed { index, s ->
                    response = context.getSharedPreferences().put(s, args.get(index), psw)
                }
                if (response) {
                    return BaseResponse(
                        LocalResultCode.RESULT_SUCCESS,
                        response,
                        "请求成功"
                    )
                } else {
                    return BaseResponse(
                        LocalResultCode.RESULT_FAILED,
                        Any(),
                        "请求失败"
                    )
                }
            }
        } else if (annotation is L_DELETE) {
            val type = annotation.type
            val key = annotation.key

            if (type == LocalType.SP) {
                var response = false
                key.forEach {
                    response = context.getSharedPreferences().remove(it)
                }
                if (response) {
                    return BaseResponse(
                        LocalResultCode.RESULT_SUCCESS,
                        response,
                        "请求成功"
                    )
                } else {
                    return BaseResponse(
                        LocalResultCode.RESULT_FAILED,
                        Any(),
                        "请求失败"
                    )
                }
            }
        }
        return BaseResponse(
            LocalResultCode.RESULT_FAILED,
            Any(),
            "请求失败"
        )
    }

    class Builder(var context: Context, var psw: String?) {
        fun build(): Localfit = Localfit(context, psw)
    }
}