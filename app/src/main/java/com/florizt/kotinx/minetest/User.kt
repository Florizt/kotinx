package com.florizt.kotinx

import java.io.File

/**
 * Created by wuwei
 * 2021/7/15
 * 佛祖保佑       永无BUG
 */
 open class User constructor(name:String?){
    var name: String? = null
        set(value) {
            field = value
        }
        get() = field


    var age: Int? = null

        set(value) {
            field = value
        }
        get() = field

    lateinit var file:File
}