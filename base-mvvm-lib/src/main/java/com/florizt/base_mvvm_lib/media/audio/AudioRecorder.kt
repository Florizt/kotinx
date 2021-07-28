package com.florizt.base_mvvm_lib.media.audio

import android.content.Context
import android.media.*
import com.florizt.base_mvvm_lib.ext.TYPE_AUDIO
import com.florizt.base_mvvm_lib.ext.createDir
import com.florizt.base_mvvm_lib.media.audio.PCMMerge.merge
import com.florizt.base_mvvm_lib.media.audio.PCMToAAC.startEncode
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


/**
 * 用于实现录音、暂停、继续、停止、播放
 * Created by wuwei
 * 2021/7/28
 * 佛祖保佑       永无BUG
 */
class AudioRecorder {
    private var context: Context? = null

    //-------------------------------------------参数start-----------------------------------------------
    //音频输入-麦克风
    private val AUDIO_INPUT = MediaRecorder.AudioSource.MIC
    /**
     * 采样率即采样频率，采样频率越高，能表现的频率范围就越大
     * 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
     */
    private val AUDIO_SAMPLE_RATE = 16000
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private val AUDIO_CHANNEL: Int = AudioFormat.CHANNEL_IN_MONO

    /**
     * 位深度也叫采样位深，音频的位深度决定动态范围
     * 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
     */
    private val AUDIO_ENCODING: Int = AudioFormat.ENCODING_PCM_16BIT
    // 缓冲区字节大小
    private val bufferSizeInBytes =
        AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING)
    //-------------------------------------------参数end-----------------------------------------------

    //-------------------------------------------实例start-----------------------------------------------
    //录音对象
    private var audioRecord: AudioRecord? = null

    /**
     * 播放声音
     * 一些必要的参数，需要和AudioRecord一一对应，否则声音会出错
     */
    private var audioTrack: AudioTrack? = null
    //-------------------------------------------实例end-----------------------------------------------

    //-------------------------------------------状态和文件start-----------------------------------------------
    //录音状态,默认未开始
    private var status = AudioStatus.STATUS_NO_READY

    private val tempDirPath: String =
        context?.createDir(TYPE_AUDIO, "pcm-temp")!!.getAbsolutePath()
    private val mergeDirPath: String =
        context?.createDir(TYPE_AUDIO, "pcm-merge")!!.getAbsolutePath()
    private val finalDirPath: String =
        context?.createDir(TYPE_AUDIO, "pcm-final")!!.getAbsolutePath()
    //合成文件
    private var originFileName: String? = null

    //录音分片文件集合
    private val tempPCMFilePaths = ArrayList<String>()
    //-------------------------------------------状态和文件end-----------------------------------------------

    //-------------------------------------------状态和文件end-----------------------------------------------
    //用来回调，转码后的文件绝对路径
    private var iAudioCallback: IAudioCallback? = null
    /**
     * 创建带有缓存的线程池
     * 当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
     * 如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private val cachedThreadPool: ExecutorService = Executors.newCachedThreadPool()

    /**
     * 单例，双重检验
     *
     * @param iAudio 用于合成后回调
     * @return
     */
    fun AudioRecorder(context: Context?, iAudio: IAudioCallback) {
        this.context = context?.getApplicationContext()
        this.iAudioCallback = iAudio
    }

    /**
     * 创建默认的录音对象
     */
    fun createDefaultAudio() {
        originFileName = SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA).format(Date())
        //创建AudioRecord
        audioRecord = AudioRecord(
            AUDIO_INPUT,
            AUDIO_SAMPLE_RATE,
            AUDIO_CHANNEL,
            AUDIO_ENCODING,
            bufferSizeInBytes
        )
        status = AudioStatus.STATUS_READY
        //创建AudioTrack
        val audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        val audioFormat: AudioFormat = AudioFormat.Builder().setSampleRate(AUDIO_SAMPLE_RATE)
            .setEncoding(AUDIO_ENCODING).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()
        audioTrack = AudioTrack(
            audioAttributes, audioFormat, bufferSizeInBytes,
            AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE
        )
    }

    /**
     * 开始录音
     */
    fun startRecord() {
        check(!(status == AudioStatus.STATUS_NO_READY)) { "请检查录音权限" }
        check(!(status == AudioStatus.STATUS_START)) { "正在录音" }
        try {
            audioRecord?.startRecording()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cachedThreadPool.execute({ writeDataToFile() })
    }

    /**
     * 暂停录音
     */
    fun pauseRecord() {
        check(!(status != AudioStatus.STATUS_START)) { "没有在录音" }
        status = AudioStatus.STATUS_PAUSE
        try {
            audioRecord?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止录音
     */
    fun stopRecord() {
        check(!(status == AudioStatus.STATUS_NO_READY || status == AudioStatus.STATUS_READY)) { "录音尚未开始" }
        status = AudioStatus.STATUS_STOP
        try {
            audioRecord?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        release()
        merge()
    }

    /**
     * 将音频信息写入文件
     */
    private fun writeDataToFile() {
        var fos: FileOutputStream? = null
        var readSize = 0
        try { // new一个byte数组用来存一些字节数据，大小为缓冲区大小
            val audioData = ByteArray(bufferSizeInBytes)
            var currentFileName = originFileName
            if (status == AudioStatus.STATUS_PAUSE) { //假如是暂停录音 将文件名后面加个数字,防止重名文件内容被覆盖
                currentFileName = currentFileName + "_" + tempPCMFilePaths.size
            }
            if (!currentFileName!!.endsWith(".pcm")) {
                currentFileName = "$currentFileName.pcm"
            }
            val file = File(tempDirPath, currentFileName)
            if (file.exists()) {
                file.delete()
            }
            tempPCMFilePaths.add(file.getAbsolutePath())
            // 建立一个可存取字节的文件
            fos = FileOutputStream(file)
            //将录音状态设置成正在录音状态
            status = AudioStatus.STATUS_START
            while (status == AudioStatus.STATUS_START) {
                readSize = audioRecord!!.read(audioData, 0, bufferSizeInBytes)
                if (AudioRecord.ERROR_INVALID_OPERATION != readSize && fos != null) {
                    try {
                        var v: Long = 0
                        val buffer = bytesToShort(audioData)
                        // 将 buffer 内容取出，进行平方和运算
                        for (i in buffer!!.indices) {
                            v += buffer[i] * buffer[i].toLong()
                        }
                        // 平方和除以数据总长度，得到音量大小。
                        val mean = v / readSize.toDouble()
                        val volume = (10 * Math.log10(mean)).toInt()
                        iAudioCallback?.onDecibelChanging(volume)
                        fos.write(audioData)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            iAudioCallback?.makeTemp(file)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            throw IllegalStateException(e.message)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos.close() // 关闭写入流
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun bytesToShort(bytes: ByteArray?): ShortArray? {
        if (bytes == null) {
            return null
        }
        val shorts = ShortArray(bytes.size / 2)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)
        return shorts
    }

    fun merge() {
        try {
            if (tempPCMFilePaths.size > 0) { //将多个pcm文件合并再转为aac/.m4a
                pcmFilesToAACFile()
            }
        } catch (e: IllegalStateException) {
            throw IllegalStateException(e.message)
        }
    }

    /**
     * 将pcm合并成aac
     */
    private fun pcmFilesToAACFile() {
        var mergeFileName = originFileName
        if (!mergeFileName!!.endsWith(".pcm")) {
            mergeFileName = "$mergeFileName.pcm"
        }
        val mergeFilePath: String = File(mergeDirPath, mergeFileName).getAbsolutePath()
        cachedThreadPool.execute({
            if (merge(tempPCMFilePaths, mergeFilePath)) {
                clearAllTemp()
                var aacFileName = originFileName
                if (!aacFileName!!.endsWith(".m4a")) {
                    aacFileName = "$aacFileName.m4a"
                }
                val aacFilePath: String = File(finalDirPath, aacFileName).getAbsolutePath()
                startEncode(mergeFilePath, aacFilePath)
                val mergeFile = File(mergeFilePath)
                if (mergeFile.exists()) {
                    mergeFile.delete()
                }
                //合成后回调
                iAudioCallback?.makeFinal(File(aacFilePath))
            } else {
                throw IllegalStateException("合成失败")
            }
        })
    }

    fun clearAllTemp() {
        for (i in tempPCMFilePaths.indices) {
            val file = File(tempPCMFilePaths[i])
            if (file.exists()) {
                file.delete()
            }
        }
    }

    /**
     * 播放合成后的wav文件
     *
     * @param filePath 文件的绝对路径
     */
    fun play(filePath: String) {
        audioTrack?.play()
        cachedThreadPool.execute({
            val file = File(filePath)
            if (!file.exists()) {
                return@execute
            }
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val buffer = ByteArray(bufferSizeInBytes)
            while (fis != null) {
                try {
                    val readCount: Int = fis.read(buffer)
                    if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                        continue
                    }
                    if (readCount != 0 && readCount != -1) {
                        audioTrack?.write(buffer, 0, readCount)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * 释放资源
     */
    fun release() {
        audioRecord?.release()
        audioRecord = null
        status = AudioStatus.STATUS_NO_READY
    }

    /**
     * 释放audioTrack
     */
    fun releaseAudioTrack() {
        if (audioTrack?.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
            audioTrack?.stop()
        }
        audioTrack?.release()
        audioTrack = null
    }

    interface IAudioCallback {
        fun makeTemp(tempPCMFile: File)
        fun makeFinal(finalPCMFile: File)
        fun onDecibelChanging(decibel: Int)
    }

    /**
     * 获取录音对象的状态
     */
    fun getStatus(): AudioStatus? {
        return status
    }
}