package com.florizt.kotinx

import android.content.Context
import com.florizt.kotinx.minetest.empty

/**
 * Created by wuwei
 * 2021/7/14
 * 佛祖保佑       永无BUG
 */

open class Demo @SuppressWarnings constructor(var name: String, var age: Int) : IDemo, BaseDemo(name) {
    fun test() {
        println(name)
    }

    constructor(name: String, age: Int, sex: Int) : this(name, age) {
        val sealdDemo = SealdDemo.A()
        val b = SealdDemo.B
    }

    override var text: String = ""
        get() = field
        set(value) {
            if (value.empty()) {
                field = "xxx"
            } else {
                field = value
            }
        }

    override fun iTest() {
        val v = object : IDemo {
            override var text: String
                get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
                set(value) {}

            override fun iTest() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        val value = object : User("a"),IDemo{
            override var text: String
                get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
                set(value) {}

            override fun iTest() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }

        val obj = object {
            var a: Int? = null
        }
        println(obj.a)
    }

    override fun baseTest() {
        super.baseTest()
        file
    }

    inner class Demo2 constructor(var ctx: Context) : AbsDemo() {
        override var content: String
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}

        override fun absTest() {
            println(name)

            iTest()
            baseTest()
        }
    }
}