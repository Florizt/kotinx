package com.florizt.base_mvvm_lib.ext

import java.io.*
import java.text.DecimalFormat

/**
 * Created by wuwei
 * 2021/7/22
 * 佛祖保佑       永无BUG
 */

fun File.copyFile(newFile: File) {
    return try {
        if (!exists()) {
            return
        } else if (!isFile()) {
            return
        } else if (!canRead()) {
            return
        }
        val fileInputStream = FileInputStream(this) //读入原文件
        val fileOutputStream = FileOutputStream(newFile)
        val buffer = ByteArray(1024)
        var byteRead: Int = 0
        while (fileInputStream.read(buffer).also({ byteRead = it }) != -1) {
            fileOutputStream.write(buffer, 0, byteRead)
        }
        fileInputStream.close()
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun File.file2Byte(): ByteArray? {
    val byte_size = 1024
    val b = ByteArray(byte_size)
    try {
        val fileInputStream = FileInputStream(this)
        val outputStream = ByteArrayOutputStream(
            byte_size
        )
        var length: Int = 0
        while (fileInputStream.read(b).also({ length = it }) != -1) {
            outputStream.write(b, 0, length)
        }
        fileInputStream.close()
        outputStream.close()
        return outputStream.toByteArray()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun File.byte2File(buf: ByteArray?): File? {
    var bos: BufferedOutputStream? = null
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(this)
        bos = BufferedOutputStream(fos)
        bos.write(buf)
        return this
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        if (bos != null) {
            try {
                bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (fos != null) {
            try {
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return null
}

const val SIZETYPE_B = 1 //获取文件大小单位为B的double值
const val SIZETYPE_KB = 2 //获取文件大小单位为KB的double值
const val SIZETYPE_MB = 3 //获取文件大小单位为MB的double值
const val SIZETYPE_GB = 4 //获取文件大小单位为GB的double值

/**
 * 获取文件的大小
 *
 * @param file 文件
 * @return String值的大小
 */
fun File.getFileSize(): String? {
    var blockSize: Long = 0
    try {
        blockSize = if (isDirectory) {
            this.getSizes()
        } else {
            this.getSize()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return FormetFileSize(blockSize)
}

/**
 * 获取文件的指定单位的大小
 *
 * @param file     文件
 * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
 * @return double值的大小
 */
fun File.getFileSize(sizeType: Int): Double {
    var blockSize: Long = 0
    try {
        blockSize = if (isDirectory) {
            this.getSizes()
        } else {
            this.getSize()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return FormetFileSize(blockSize, sizeType)
}

/**
 * 获取指定文件大小
 *
 * @param
 * @return
 * @throws Exception
 */
@Throws(java.lang.Exception::class)
fun File.getSize(): Long {
    var size: Long = 0
    if (exists()) {
        var fis: FileInputStream? = null
        fis = FileInputStream(this)
        size = fis.available().toLong()
    } else {
        this.createNewFile()
    }
    return size
}

/**
 * 获取指定文件夹
 *
 * @param f
 * @return
 * @throws Exception
 */
@Throws(java.lang.Exception::class)
fun File.getSizes(): Long {
    var size: Long = 0
    val flist = listFiles()
    for (i in flist.indices) {
        size = if (flist[i].isDirectory) {
            size + flist[i].getSizes()
        } else {
            size + flist[i].getSize()
        }
    }
    return size
}

/**
 * 转换文件大小
 *
 * @param fileS
 * @return
 */
private fun FormetFileSize(fileS: Long): String? {
    val df = DecimalFormat("#.00")
    var fileSizeString = ""
    val wrongSize = "0B"
    if (fileS == 0L) {
        return wrongSize
    }
    fileSizeString = if (fileS < 1024) {
        df.format(fileS.toDouble()).toString() + "B"
    } else if (fileS < 1048576) {
        df.format(fileS.toDouble() / 1024).toString() + "KB"
    } else if (fileS < 1073741824) {
        df.format(fileS.toDouble() / 1048576).toString() + "MB"
    } else {
        df.format(fileS.toDouble() / 1073741824).toString() + "GB"
    }
    return fileSizeString
}

/**
 * 转换文件大小,指定转换的类型
 *
 * @param fileS
 * @param sizeType
 * @return
 */
private fun FormetFileSize(fileS: Long, sizeType: Int): Double {
    val df = DecimalFormat("#.00")
    var fileSizeLong = 0.0
    when (sizeType) {
        SIZETYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble()))
        SIZETYPE_KB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1024))
        SIZETYPE_MB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1048576))
        SIZETYPE_GB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1073741824))
        else -> {
        }
    }
    return fileSizeLong
}