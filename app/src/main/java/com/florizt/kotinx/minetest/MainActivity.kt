package com.florizt.kotinx

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.florizt.kotinx.minetest.setOnClickListeners
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {

    suspend fun c() {
        GlobalScope.launch {
            delay(1)
            launch { }

            withContext(Dispatchers.IO) {
                var data: String? = null
            }

        }

        coroutineScope {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //View.kt
        var mOnClickListener: ((View) -> Boolean)? = null
        var mOnContextClickListener: ((View) -> Unit)? = null

        fun setOnClickListener(l: (View) -> Boolean) {
            mOnClickListener = l;
//            mOnClickListener.invoke()
        }

        fun setOnContextClickListener(l: (View) -> Unit) {
            mOnContextClickListener = l;
        }

        val view = View(this@MainActivity)
        println("----000-------$view")
        view.setOnClickListeners({
            println("----111-------$it")
            return@setOnClickListeners true
        }, {
            println("----222-------$this----$it")
            return@setOnClickListeners true
        })


        /*test14()

        val demo = Demo("aa", 1)

        val t = TextView(this@MainActivity)
        t.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                println("aa")
                println("bb")
            }
        })

        t.setOnClickListener {
            println("aa")
            println("bb")
        }

        t.setOnTouchListener { view, motionEvent ->
            println("aa")
            println("bb")
            return@setOnTouchListener true
        }

        println(ByProxy(ByImpl()).test())

        var a: Int by ByDelegate()*/
    }

    fun test(a: Int): String {
        return "";
    }

    fun test1(a: Int): Int = 1;

    var a: String? = null;

    fun test2(a: Int, b: Int): Int {
        if (a > b) {
            return a
        } else {
            return b
        }
    }

    fun test3(a: Int, b: Int): Int = if (a > b) a else b

    fun test4(a: String?): String {
        return a?.length.toString() ?: "0";
    }

    fun test5(file: File?): String {
        return file?.absolutePath ?: "xx";
    }

    fun test6(file: File?) {
        file ?: throw IllegalArgumentException()
    }

    fun test7(s: String?): Int? = s?.toInt() ?: 0

    fun test8(s1: String, s2: String): Int {
        val i1 = test7(s1)
        val i2 = test7(s2)

        return if (i1 != null && i2 != null) i1 * i2 else 0
    }

    fun test9(s: Any): Int = when (s) {
        "1" -> 1
        "2" -> 2
        is Demo -> 3
        Config.A -> 4
        else -> 0
    }

    fun test10(file: File?) {
        var let = file.let {
            it?.absolutePath
            it?.name
        }

        val also = file.also {
            it?.absolutePath
            it?.name
            999
        }

        with(file) {
            this?.absolutePath
            this@MainActivity.test5(file)
            this?.name
        }

        file.run {
            this?.absolutePath
            this?.name
        }

        file.apply {
            this?.absoluteFile
            this?.absolutePath
            this?.name
        }
    }

    fun test11(file: File?): String = file.let { it?.absolutePath } ?: ""

    fun test12() {
        var a = 1;
        var b = 2;
        a = b.also { b = a }
    }

    fun test13(obj: Any): String? {
        return if (obj is String) obj else null
    }

    fun test14() {
        val list = listOf("a", "b", "c")
        for (li in list) {
            println(li)
        }

        for (index in list.indices) {
            println("$index ---${list.get(index)}")
        }

        list.forEach {

        }

        val map = mapOf("a" to "aa", "b" to "bb", 1 to "cc")
        map.forEach {
            println("${it.key} ${it.value}")
        }

        map.entries.forEach {
            println("${it.key} ${it.value}")
        }
    }

    fun test15(a: Int): Int {
        return if (a in 1..10) a else 0
    }
}
