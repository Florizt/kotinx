package com.florizt.base_mvvm_lib.media.camera

/**
 * Created by wuwei
 * 2021/7/28
 * 佛祖保佑       永无BUG
 */
enum class VideoStatus {
    //未开始
    STATUS_NO_READY,
    //预备
    STATUS_READY,
    //录制
    STATUS_START,
    //暂停
    STATUS_PAUSE,
    //停止
    STATUS_STOP
}