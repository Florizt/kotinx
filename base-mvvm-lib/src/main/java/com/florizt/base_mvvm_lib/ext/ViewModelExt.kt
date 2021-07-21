package com.florizt.base_mvvm_lib.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */

fun ViewModel.launchUI(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.Main) { block() }
}

suspend fun ViewModel.launchWithUI(block: suspend CoroutineScope.() -> Unit) {
    withContext(Dispatchers.Main) { block() }
}

fun ViewModel.launchIO(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.IO) { block() }
}

suspend fun ViewModel.launchWithIO(block: suspend CoroutineScope.() -> Unit) {
    withContext(Dispatchers.IO) { block() }
}