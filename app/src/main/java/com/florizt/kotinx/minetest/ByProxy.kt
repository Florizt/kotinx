package com.florizt.kotinx

/**
 * Created by wuwei
 * 2021/7/15
 * 佛祖保佑       永无BUG
 */
class ByProxy constructor(by:IBy) : IBy by by{
    override fun test(): Int {
        return 2
    }
}