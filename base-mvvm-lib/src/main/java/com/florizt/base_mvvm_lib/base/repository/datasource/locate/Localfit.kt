package com.florizt.base_mvvm_lib.base.repository.datasource.locate

import android.content.Context
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
                if (method.declaringClass === Any::class.java) {
                    method.invoke(this, args)
                }

                val annotations = method.annotations
                var o: Any? = null
                for (annotation in annotations) {
                    o = parseMethodAnnotation(method, annotation, args, context, psw)
                    if (o != null) {
                        break
                    }
                }
                o
            }) as S
    }

    fun parseMethodAnnotation(
        method: Method,
        annotation: Annotation?,
        args: Array<Any>?,
        context: Context,
        psw: String?
    ): Any? {
        if (annotation is L_GET) {
            val type = annotation.type
            val key = annotation.key
            if (type == LocalType.SP) {
                return context.getSharedPreferences()
                    .getDecrypt(key[0], psw, method.kotlinFunction?.returnType);
            }
        } else if (annotation is L_POST) {
            val type = annotation.type
            val key = annotation.key
            if (key.size != args?.size) {
                throw IllegalArgumentException("annotation SP key must match args");
            }

            if (type == LocalType.SP) {
                key.forEachIndexed { index, s ->
                    context.getSharedPreferences().put(s, args?.get(index), psw)
                }
            }
        } else if (annotation is L_PUT) {
            val type = annotation.type
            val key = annotation.key
            if (key.size != args?.size) {
                throw IllegalArgumentException("annotation SP key must match args");
            }

            if (type == LocalType.SP) {
                key.forEachIndexed { index, s ->
                    context.getSharedPreferences().put(s, args.get(index), psw)
                }
            }
        } else if (annotation is L_DELETE) {
            val type = annotation.type
            val key = annotation.key

            if (type == LocalType.SP) {
                key.forEach { context.getSharedPreferences().remove(it) }
            }
        }
        return null;
    }

    class Builder(var context: Context, var psw: String?) {
        fun build(): Localfit = Localfit(context, psw)
    }
}