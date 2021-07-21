package com.florizt.base_mvvm_lib.ext

import android.util.Base64
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */

/**
 * 加密
 *
 * @param data 加密数据
 * @return
 * @throws Exception
 */
val CHARSET: Charset = charset("UTF-8")

const val ALGORITHM_3DES: String = "DESede"
const val ALGORITHM_MD5: String = "MD5"

@Throws(Exception::class)
fun String.encrypt3DES(psw: String): String? { // 恢复密钥
    val secretKey: SecretKey = SecretKeySpec(build3Deskey(psw.toByteArray()), ALGORITHM_3DES)
    // Cipher完成加密
    val cipher: Cipher = Cipher.getInstance(ALGORITHM_3DES)
    // cipher初始化
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encrypt: ByteArray = cipher.doFinal(toByteArray())
    //转码
    return String(Base64.encode(encrypt, Base64.DEFAULT), CHARSET)
}

/**
 * 解密
 *
 * @param data 加密后的字符串
 * @return
 * @throws Exception
 */
@Throws(Exception::class)
fun String.decrypt3DES(psw: String): String? { // 恢复密钥
    val secretKey: SecretKey = SecretKeySpec(build3Deskey(psw.toByteArray()), ALGORITHM_3DES)
    // Cipher完成解密
    val cipher: Cipher = Cipher.getInstance(ALGORITHM_3DES)
    // 初始化cipher
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    //转码
    val bytes: ByteArray = Base64.decode(toByteArray(CHARSET), Base64.DEFAULT)
    //解密
    val plain: ByteArray = cipher.doFinal(bytes)
    //解密结果转码
    return String(plain, CHARSET)
}

@Throws(Exception::class)
private fun build3Deskey(temp: ByteArray): ByteArray? {
    val key = ByteArray(24)
    if (key.size > temp.size) {
        System.arraycopy(temp, 0, key, 0, temp.size)
    } else {
        System.arraycopy(temp, 0, key, 0, key.size)
    }
    return key
}

@Throws(NoSuchAlgorithmException::class)
fun String.toMD5(): String? { //获取摘要器 MessageDigest
    val messageDigest: MessageDigest = MessageDigest.getInstance(ALGORITHM_MD5)
    //通过摘要器对字符串的二进制字节数组进行hash计算
    val digest: ByteArray = messageDigest.digest(toByteArray())
    val sb = java.lang.StringBuilder()
    for (i in digest.indices) { //循环每个字符 将计算结果转化为正整数;
        val digestInt: Int = digest[i].toInt() and 0xff
        //将10进制转化为较短的16进制
        val hexString = Integer.toHexString(digestInt)
        //转化结果如果是个位数会省略0,因此判断并补0
        if (hexString.length < 2) {
            sb.append(0)
        }
        //将循环结果添加到缓冲区
        sb.append(hexString)
    }
    //返回整个结果
    return sb.toString()
}