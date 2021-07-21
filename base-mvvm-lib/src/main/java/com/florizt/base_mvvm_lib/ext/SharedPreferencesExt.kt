package com.florizt.base_mvvm_lib.ext

import android.content.SharedPreferences
import android.util.Base64
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
fun SharedPreferences.put(
    key: String,
    value: Any
) = put(key, value, null)

fun SharedPreferences.put(
    key: String,
    value: Any,
    psw: String?
) {
    val editor = edit()
    var bos: ByteArrayOutputStream? = null
    var oos: ObjectOutputStream? = null
    try {
        var v: String? = null
        when (value) {
            value is String,
            value is Long,
            value is Boolean,
            value is Float,
            value is Double,
            value is Int,
            value is ByteArray -> v = value.toString()
            else -> {
                bos = ByteArrayOutputStream()
                oos = ObjectOutputStream(bos)
                oos.writeObject(value)
                val bytes: ByteArray = bos.toByteArray()
                v = Base64.encodeToString(bytes, Base64.DEFAULT)
            }
        }
        psw?.also { v = v?.encrypt3DES(psw) }
        editor.putString(key, v)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        SharedPreferencesCompat.apply(editor)
        try {
            if (bos != null) {
                bos.close()
            }
            if (oos != null) {
                oos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 不解密获取
 *
 * @param key     键
 * @param convert 转换类型
 * @return 值
 */
fun SharedPreferences.get(
    key: String,
    convert: Class<*>
): Any? =
    getDecrypt(key, null, convert)

/**
 * 解密获取
 *
 * @param key     键
 * @param psw     解密密码
 * @param convert 转换类型
 * @return 值
 */
fun SharedPreferences.getDecrypt(
    key: String,
    psw: String?,
    convert: Class<*>
): Any? {
    var v: String? = getString(key, "")
    var bis: ByteArrayInputStream? = null
    var ois: ObjectInputStream? = null
    try {
        psw?.also { v = v?.decrypt3DES(psw) }
        return if (convert == String::class.java) {
            v
        } else if (convert == Long::class.java || convert == Long::class.javaPrimitiveType) {
            v?.toLong()
        } else if (convert == Boolean::class.java || convert == Boolean::class.javaPrimitiveType) {
            v?.toBoolean()
        } else if (convert == Float::class.java || convert == Float::class.javaPrimitiveType) {
            v?.toFloat()
        } else if (convert == Double::class.java || convert == Double::class.javaPrimitiveType) {
            v?.toDouble()
        } else if (convert == Int::class.java || convert == Int::class.javaPrimitiveType) {
            v?.toInt()
        } else if (convert == Array<Byte>::class.java || convert == ByteArray::class.java) {
            v?.toByteArray()
        } else {
            val bytes =
                Base64.decode(v, Base64.DEFAULT)
            bis = ByteArrayInputStream(bytes)
            ois = ObjectInputStream(bis)
            ois.readObject()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (bis != null) {
                bis.close()
            }
            if (ois != null) {
                ois.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return v
}

/**
 * 移除某个key值已经对应的值
 */
fun SharedPreferences.remove(key: String) {
    val editor: SharedPreferences.Editor = edit()
    editor.remove(key)
    SharedPreferencesCompat.apply(editor)
}

/**
 * 清除所有数据
 */
fun SharedPreferences.clear() {
    val editor: SharedPreferences.Editor = edit()
    editor.clear()
    SharedPreferencesCompat.apply(editor)
}

/**
 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
 */
private object SharedPreferencesCompat {
    private val sApplyMethod: Method? = findApplyMethod()
    /**
     * 反射查找apply的方法
     */
    private fun findApplyMethod(): Method? {
        try {
            val clz: Class<*> = SharedPreferences.Editor::class.java
            return clz.getMethod("apply")
        } catch (e: NoSuchMethodException) {
        }
        return null
    }

    /**
     * 如果找到则使用apply执行，否则使用commit
     */
    fun apply(editor: SharedPreferences.Editor) {
        try {
            if (sApplyMethod != null) {
                sApplyMethod.invoke(editor)
                return
            }
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        } catch (e: InvocationTargetException) {
        }
        editor.commit()
    }
}