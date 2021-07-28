package com.florizt.base_mvvm_lib.media.audio

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import java.io.*
import java.nio.ByteBuffer


/**
 * Created by wuwei
 * 2021/7/28
 * 佛祖保佑       永无BUG
 */
object PCMToAAC {
    /**
     * pcm to aac
     *
     * @param pcmPath pcm文件路径
     * @param aacPath 目标aac文件路径
     */
    fun startEncode(pcmPath: String, aacPath: String) {
        try { //参数对应-> mime type、采样率、声道数
            val encodeFormat =
                MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 16000, 1)
            encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000) //比特率
            encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
            encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO)
            encodeFormat.setInteger(
                MediaFormat.KEY_AAC_PROFILE,
                MediaCodecInfo.CodecProfileLevel.AACObjectLC
            )
            encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 2048) //作用于inputBuffer的大小
            val mediaEncode =
                MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            mediaEncode.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mediaEncode.start()
            readInputStream(pcmPath, aacPath, mediaEncode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readInputStream(
        pcmPath: String,
        aacPath: String,
        mediaCodec: MediaCodec
    ) {
        var inputStream: InputStream? = null
        var outputStream: ByteArrayOutputStream? = null
        var out: BufferedOutputStream? = null
        try {
            inputStream = FileInputStream(pcmPath)
            outputStream = ByteArrayOutputStream()
            out = BufferedOutputStream(FileOutputStream(aacPath, false))
            val buffer = ByteArray(1024)
            while (inputStream.read(buffer) !== -1) {
                dstAudioFormatFromPCM(buffer, out, mediaCodec)
                Log.e("wqs+readInputStream", "readInputStream: $buffer")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (inputStream != null) {
                    inputStream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 编码PCM数据 得到AAC格式的音频文件
     */
    private fun dstAudioFormatFromPCM(
        pcmData: ByteArray,
        outputStream: BufferedOutputStream?,
        mediaCodec: MediaCodec
    ) {
        val inputIndex: Int
        val inputBuffer: ByteBuffer
        var outputIndex: Int
        var outputBuffer: ByteBuffer
        var outBitSize: Int
        var outPacketSize: Int
        val PCMAudio: ByteArray
        PCMAudio = pcmData
        val encodeInputBuffers: Array<ByteBuffer> = mediaCodec.inputBuffers
        val encodeOutputBuffers: Array<ByteBuffer> = mediaCodec.outputBuffers
        val encodeBufferInfo = MediaCodec.BufferInfo()
        inputIndex = mediaCodec.dequeueInputBuffer(0)
        if (inputIndex != -1) {
            inputBuffer = encodeInputBuffers[inputIndex]
            inputBuffer.clear()
            inputBuffer.limit(PCMAudio.size)
            inputBuffer.put(PCMAudio) //PCM数据填充给inputBuffer
            mediaCodec.queueInputBuffer(inputIndex, 0, PCMAudio.size, 0, 0) //通知编码器 编码
            outputIndex = mediaCodec.dequeueOutputBuffer(encodeBufferInfo, 0)
            while (outputIndex > 0) {
                outBitSize = encodeBufferInfo.size
                outPacketSize = outBitSize + 7 //7为ADT头部的大小
                outputBuffer = encodeOutputBuffers[outputIndex] //拿到输出Buffer
                outputBuffer.position(encodeBufferInfo.offset)
                outputBuffer.limit(encodeBufferInfo.offset + outBitSize)
                val chunkAudio = ByteArray(outPacketSize)
                addADTStoPacket(chunkAudio, outPacketSize) //添加ADTS
                outputBuffer.get(chunkAudio, 7, outBitSize) //将编码得到的AAC数据 取出到byte[]中
                try { //录制aac音频文件，保存在手机内存中
                    outputStream?.write(chunkAudio, 0, chunkAudio.size)
                    outputStream?.flush()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                outputBuffer.position(encodeBufferInfo.offset)
                mediaCodec.releaseOutputBuffer(outputIndex, false)
                outputIndex = mediaCodec.dequeueOutputBuffer(encodeBufferInfo, 0)
            }
        }
    }

    /**
     * 添加ADTS头
     *
     * @param packet
     * @param packetLen
     */
    private fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        val profile = 2 // AAC LC
        val freqIdx = 8 // 16KHz
        val chanCfg = 1 // CPE
        // fill in ADTS data
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF1.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }
}