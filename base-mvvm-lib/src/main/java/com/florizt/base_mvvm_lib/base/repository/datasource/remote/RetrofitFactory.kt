package com.florizt.base_mvvm_lib.base.repository.datasource.remote

import com.florizt.base_mvvm_lib.base.BaseApp
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by wuwei
 * 2021/7/16
 * 佛祖保佑       永无BUG
 */
/**
 * 工厂方式获取[Retrofit]
 * 可通过koin进行注入
 */
object RetrofitFactory {

    const val BASE_URL = "https://www.baidu.com/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(15000, TimeUnit.MILLISECONDS)
            .writeTimeout(15000, TimeUnit.MILLISECONDS)
            .connectTimeout(15000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .cache(
                Cache(
                    File(BaseApp.instance.getContext()?.cacheDir, "cache"),
                    1024 * 1024 * 100
                )
            )
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <S> getService(service: Class<S>): S = retrofit.create(service)
}