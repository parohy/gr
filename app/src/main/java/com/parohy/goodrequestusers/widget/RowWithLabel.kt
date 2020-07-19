package com.parohy.goodrequestusers.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.parohy.goodrequestusers.R
import kotlinx.android.synthetic.main.row_with_label.view.*

class RowWithLabel: LinearLayout {
    constructor(context: Context): super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null) {
        View.inflate(context, R.layout.row_with_label, this)
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RowWithLabel)
            label = typedArray.getString(R.styleable.RowWithLabel_label)
            text = typedArray.getString(R.styleable.RowWithLabel_text)
            typedArray.recycle()
        }
    }

    var text: CharSequence? = null
        get() = rowValue.text
        set(value) {
            rowValue.text = value
            field = value
        }

    var label: CharSequence? = null
        get() = rowLabel.text
        set(value) {
            rowLabel.text = value
            field = value
        }
}