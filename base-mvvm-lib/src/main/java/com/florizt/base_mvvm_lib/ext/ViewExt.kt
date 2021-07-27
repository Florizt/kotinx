package com.florizt.base_mvvm_lib.ext

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.forEach
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


/**
 * Created by wuwei
 * 2021/7/20
 * 佛祖保佑       永无BUG
 */
@BindingAdapter(value = arrayOf("app:onClickCommand", "app:isThrottleFirst"), requireAll = false)
fun View.onClickCommand(onClickCommand: () -> Unit, isThrottleFirst: Boolean) {
    val minTime = 500L
    var lastTime = 0L
    setOnClickListener {
        val tmpTime = System.currentTimeMillis()
        if (isThrottleFirst) {
            onClickCommand()
        } else {
            if (tmpTime - lastTime > minTime) {
                onClickCommand()
                lastTime = tmpTime
            }
        }

    }
}

@BindingAdapter(value = arrayOf("app:onLongClickCommand"))
fun View.onLongClickCommand(onLongClickCommand: () -> Unit) {
    setOnLongClickListener { v ->
        onLongClickCommand()
        false
    }
}

// 0-IDLE,1-LOADING, 2-NODATA,3-ERROR,4-SUCCESS
const val IDLE = 0
const val LOADING = 1
const val NODATA = 2
const val ERROR = 3
const val SUCCESS = 4
@BindingAdapter(
    value = arrayOf(
        "app:status",
        "app:view_loading",
        "app:view_nodata",
        "app:view_error",
        "app:error_click"
    ), requireAll = false
)
fun ViewGroup.statusView(
    status: Int,
    view_loading: Int?,
    view_nodata: Int?,
    view_error: Int?,
    errorClick: () -> Unit
) {
    if (childCount == 1) {
        getChildAt(0).tag = "view_success"
    }
    val layoutInflater = LayoutInflater.from(context)
    when (status) {
        IDLE,
        SUCCESS -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_loading")
                    || TextUtils.equals(it.tag as String, "view_nodata")
                    || TextUtils.equals(it.tag as String, "view_error")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.VISIBLE
                }
            }
        }
        LOADING -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_nodata")
                    || TextUtils.equals(it.tag as String, "view_error")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.GONE
                }
            }
            view_loading?.let {
                val loadingView = layoutInflater.inflate(it, null, false)
                loadingView.tag = "view_loading"
                addView(
                    loadingView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        }
        NODATA -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_loading")
                    || TextUtils.equals(it.tag as String, "view_error")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.GONE
                }
            }
            view_nodata?.let {
                val nodataView = layoutInflater.inflate(it, null, false)
                nodataView.tag = "view_nodata"
                addView(
                    nodataView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        }
        ERROR -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_loading")
                    || TextUtils.equals(it.tag as String, "view_nodata")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.GONE
                }
            }
            view_error?.let {
                val errorView = layoutInflater.inflate(it, null, false)
                errorView.tag = "view_error"
                addView(
                    errorView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                errorView.onClickCommand(errorClick, false)
            }
        }
        else -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_loading")
                    || TextUtils.equals(it.tag as String, "view_nodata")
                    || TextUtils.equals(it.tag as String, "view_error")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.VISIBLE
                }
            }
        }
    }
}

/**
 * view是否需要获取焦点
 */
@BindingAdapter("app:requestFocus")
fun View.requestFocusCommand(needRequestFocus: Boolean) {
    if (needRequestFocus) {
        isFocusableInTouchMode = true
        requestFocus()
    } else {
        clearFocus()
    }
}

/**
 * view的焦点发生变化的事件绑定
 */
@BindingAdapter("app:onFocusChangeCommand")
fun View.onFocusChangeCommand(onFocusChangeCommand: () -> Unit) {
    setOnFocusChangeListener { view, b -> onFocusChangeCommand() }
}

/**
 * view的显示隐藏
 */
@BindingAdapter("app:isVisible")
fun View.isVisible(visibility: Boolean) {
    if (visibility) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

@BindingAdapter(value = arrayOf("app:layout_width", "app:layout_height"), requireAll = false)
fun View.setLayoutWidth(width: Float, height: Float) {
    if (width != 0f) {
        val params = layoutParams
        params.width = width.toInt()
        layoutParams = params
    }
    if (height != 0f) {
        val params = layoutParams
        params.height = height.toInt()
        layoutParams = params
    }
}

@BindingAdapter("app:selected")
fun View.setSeleted(selected: Boolean) {
    isSelected = selected
}

@BindingAdapter("app:enable")
fun View.setEnable(enable: Boolean) {
    isEnabled = enable
}

@BindingAdapter(
    value = arrayOf("app:margin_top", "app:margin_bottom", "app:margin_left", "app:margin_right"),
    requireAll = false
)
fun View.setLayoutMargins(
    marginTop: Int,
    marginBottom: Int,
    marginLeft: Int,
    marginRight: Int
) {
    val params = layoutParams
    if (params is LinearLayout.LayoutParams) {
        params.topMargin = marginTop
        params.bottomMargin = marginBottom
        params.leftMargin = marginLeft
        params.rightMargin = marginRight
    } else if (params is RelativeLayout.LayoutParams) {
        params.topMargin = marginTop
        params.bottomMargin = marginBottom
        params.leftMargin = marginLeft
        params.rightMargin = marginRight
    } else if (params is FrameLayout.LayoutParams) {
        params.topMargin = marginTop
        params.bottomMargin = marginBottom
        params.leftMargin = marginLeft
        params.rightMargin = marginRight
    } else if (params is RecyclerView.LayoutParams) {
        params.topMargin = marginTop
        params.bottomMargin = marginBottom
        params.leftMargin = marginLeft
        params.rightMargin = marginRight
    }
    layoutParams = params
}

@BindingAdapter("app:text_color")
fun TextView.setTextColor(colorId: Int) {
    setTextColor(colorId)
}

@BindingAdapter("app:textSize")
fun TextView.setTextSize(size: Int) {
    textSize = size.toFloat()
}

@BindingAdapter("app:textStyle")
fun TextView.setTextStyle(style: Int) {
    if (style == 0) {
        typeface = Typeface.DEFAULT
    } else if (style == 1) {
        typeface = Typeface.DEFAULT_BOLD
    } else {
        typeface = Typeface.DEFAULT
    }
}

@BindingAdapter("app:movementMethod")
fun TextView.setMovementMethod(movementMethod: Boolean) {
    if (movementMethod) {
        setMovementMethod(LinkMovementMethod.getInstance())
    }
}

@BindingAdapter("app:flag")
fun TextView.setFlag(flag: Int) {
    paint.flags = flag or Paint.ANTI_ALIAS_FLAG //中划线
}

/**
 * EditText重新获取焦点的事件绑定
 */
@BindingAdapter("app:requestFocus")
fun EditText.requestFocusCommand(needRequestFocus: Boolean) {
    if (needRequestFocus) {
        setSelection(text.length)
        requestFocus()
        context.showSoftKeyboard(this)
    }
    isFocusableInTouchMode = needRequestFocus
}

/**
 * EditText输入文字改变的监听
 */
@BindingAdapter("app:textChanged")
fun EditText.addTextChangedListener(textChanged: () -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            i: Int,
            i1: Int,
            i2: Int
        ) {
        }

        override fun onTextChanged(text: CharSequence, i: Int, i1: Int, i2: Int) {
            textChanged()
        }

        override fun afterTextChanged(editable: Editable) {}
    })
}

@BindingAdapter(
    value = arrayOf("app:url", "app:placeholderRes", "app:strategy"),
    requireAll = false
)
fun ImageView.setImageUri(
    url: Any?,
    placeholderRes: Drawable?,
    strategy: DiskCacheStrategy?
) { //使用Glide框架加载图片
    val requestManager = Glide.with(context)
    var load: RequestBuilder<Drawable?>? = null
    if (url is String) {
        load = requestManager.load(url)
    } else if (url is Int) {
        load = requestManager.load(url)
    } else if (url is Drawable) {
        load = requestManager.load(url)
    }
    load?.let {
        if (placeholderRes != null) {
            it.apply(RequestOptions().placeholder(placeholderRes))
        }
        if (strategy != null) {
            it.diskCacheStrategy(strategy)
        }
        it.into(this)
    }
}

const val RECYCLERVIEW_LINEAR: Int = 0
const val RECYCLERVIEW_GRID: Int = 1

@BindingAdapter(
    value = arrayOf(
        "app:type", "app:orientation", "app:reverseLayout",
        "app:canScrollVertically", "app:canScrollHorizontally", "app:spanCount"
    ),
    requireAll = false
)
fun RecyclerView.setLayoutManager(
    type: Int,
    orientation: Int = LinearLayoutManager.HORIZONTAL,
    reverseLayout: Boolean = false,
    canScrollVertically: Boolean = true,
    canScrollHorizontally: Boolean = false,
    spanCount: Int = 0
) {
    if (type == RECYCLERVIEW_LINEAR) {
        layoutManager = object : LinearLayoutManager(context, orientation, reverseLayout) {
            override fun canScrollVertically(): Boolean {
                return canScrollVertically
            }

            override fun canScrollHorizontally(): Boolean {
                return canScrollHorizontally
            }
        }
    } else if (type == RECYCLERVIEW_GRID) {
        layoutManager = object : GridLayoutManager(context, spanCount, orientation, reverseLayout) {
            override fun canScrollVertically(): Boolean {
                return canScrollVertically
            }

            override fun canScrollHorizontally(): Boolean {
                return canScrollHorizontally
            }
        }
    }
}

@BindingAdapter("app:linearSnapHelper")
fun RecyclerView.setLinearSnapHelper(snapHelper: Boolean) {
    if (snapHelper) {
        val linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(this)
    }
}
