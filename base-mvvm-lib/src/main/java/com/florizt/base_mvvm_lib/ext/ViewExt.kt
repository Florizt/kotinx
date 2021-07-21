package com.florizt.base_mvvm_lib.ext

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
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
@BindingAdapter(value = arrayOf("onClickCommand", "isThrottleFirst"), requireAll = false)
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

@BindingAdapter(value = arrayOf("onLongClickCommand"))
fun View.onLongClickCommand(onLongClickCommand: () -> Unit) {
    setOnLongClickListener { v ->
        onLongClickCommand()
        false
    }
}

/**
 * view是否需要获取焦点
 */
@BindingAdapter("requestFocus")
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
@BindingAdapter("onFocusChangeCommand")
fun View.onFocusChangeCommand(onFocusChangeCommand: () -> Unit) {
    setOnFocusChangeListener { view, b -> onFocusChangeCommand() }
}

/**
 * view的显示隐藏
 */
@BindingAdapter("isVisible")
fun View.isVisible(visibility: Boolean) {
    if (visibility) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

@BindingAdapter(value = arrayOf("layout_width", "layout_height"), requireAll = false)
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

@BindingAdapter("selected")
fun View.setSeleted(selected: Boolean) {
    isSelected = selected
}

@BindingAdapter("enable")
fun View.setEnable(enable: Boolean) {
    isEnabled = enable
}

@BindingAdapter(
    value = arrayOf("margin_top", "margin_bottom", "margin_left", "margin_right"),
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

@BindingAdapter("text_color")
fun TextView.setTextColor(colorId: Int) {
    setTextColor(colorId)
}

@BindingAdapter("textSize")
fun TextView.setTextSize(size: Int) {
    textSize = size.toFloat()
}

@BindingAdapter("textStyle")
fun TextView.setTextStyle(style: Int) {
    if (style == 0) {
        typeface = Typeface.DEFAULT
    } else if (style == 1) {
        typeface = Typeface.DEFAULT_BOLD
    } else {
        typeface = Typeface.DEFAULT
    }
}

@BindingAdapter("movementMethod")
fun TextView.setMovementMethod(movementMethod: Boolean) {
    if (movementMethod) {
        setMovementMethod(LinkMovementMethod.getInstance())
    }
}

@BindingAdapter("flag")
fun TextView.setFlag(flag: Int) {
    paint.flags = flag or Paint.ANTI_ALIAS_FLAG //中划线
}

/**
 * EditText重新获取焦点的事件绑定
 */
@BindingAdapter("requestFocus")
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
@BindingAdapter("textChanged")
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

@BindingAdapter(value = arrayOf("url", "placeholderRes", "strategy"), requireAll = false)
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
        "type", "orientation", "reverseLayout",
        "canScrollVertically", "canScrollHorizontally", "spanCount"
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

@BindingAdapter("linearSnapHelper")
fun RecyclerView.setLinearSnapHelper(snapHelper: Boolean) {
    if (snapHelper) {
        val linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(this)
    }
}
