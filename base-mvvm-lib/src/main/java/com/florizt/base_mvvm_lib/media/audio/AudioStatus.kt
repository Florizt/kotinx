package com.florizt.base_mvvm_lib.media.audio

/**
 * Created by wuwei
 * 2021/7/28
 * 佛祖保佑       永无BUG
 */
enum class AudioStatus {
    //未开始
    STATUS_NO_READY,
    //预备
    STATUS_READY,
    //录音
    STATUS_START,
    //暂停
    STATUS_PAUSE,
    //停止
    STATUS_STOP
}