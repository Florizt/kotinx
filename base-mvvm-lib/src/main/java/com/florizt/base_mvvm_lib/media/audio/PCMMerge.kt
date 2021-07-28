package com.florizt.base_mvvm_lib.media.audio

import java.io.*


/**
 * Created by wuwei
 * 2021/7/28
 * 佛祖保佑       永无BUG
 */
object PCMMerge {
    fun merge(
        filePathList: List<String?>,
        destinationPath: String?
    ): Boolean {
        val file: Array<File?> = arrayOfNulls<File>(filePathList.size)
        var buffer: ByteArray? = null
        for (i in filePathList.indices) {
            file[i] = File(filePathList[i])
        }
        //先删除目标文件
        val destFile = File(destinationPath)
        if (destFile.exists()) {
            destFile.delete()
        }
        //合成所有的pcm文件的数据，写到目标文件
        try {
            buffer = ByteArray(1024 * 4) // Length of All Files, Total Size
            var inStream: InputStream? = null
            var ouStream: OutputStream? = null
            ouStream = BufferedOutputStream(FileOutputStream(destinationPath))
            for (j in filePathList.indices) {
                inStream = BufferedInputStream(FileInputStream(file[j]))
                var size: Int = inStream.read(buffer)
                while (size != -1) {
                    ouStream.write(buffer)
                    size = inStream.read(buffer)
                }
                inStream.close()
            }
            ouStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}