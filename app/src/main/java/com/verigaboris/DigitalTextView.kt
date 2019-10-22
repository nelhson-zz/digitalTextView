package com.verigaboris

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import com.verigaboris.digitaltextview.R
import com.verigaboris.utils.DimensionsUtil
import com.verigaboris.utils.countDigits
import java.util.*

private const val DIGITAL_NUMBER_FONT_PATH = "fonts/digital_strong.ttf"
private const val DEFAULT_DIGITAL_TEXT_COLOR = Color.WHITE
private const val DEFAULT_BOTTOM_BOUND = 0
private const val DEFAULT_UPPER_BOUND = 100
private const val DEFAULT_FLICKERING_ANIMATION_DURATION = 250L
private const val DEFAULT_NUMBER_TEXT_SIZE = 32F


class DigitalTextView : FrameLayout {
    private var _digitCount: Int = 0
    private var _bottomBound: Int = DEFAULT_BOTTOM_BOUND
    private var _upperBound: Int = DEFAULT_UPPER_BOUND
    private val _randomizer = Random()
    private val _digitalTextHandler = Handler()
    private var _number: Int = 0
    private var textView: TextView = TextView(context)
    private var _flickeringAnimationDuration = DEFAULT_FLICKERING_ANIMATION_DURATION
    private var _isFlickering = false
    private var digitalTextColor = DEFAULT_DIGITAL_TEXT_COLOR

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs,
            R.styleable.DigitalTextView
        )
        val numberTextSize = typedArray.getDimension(
            R.styleable.DigitalTextView_textSize,
            DEFAULT_NUMBER_TEXT_SIZE
        )
        this._flickeringAnimationDuration = typedArray.getInt(
            R.styleable.DigitalTextView_animationDuration,
            DEFAULT_FLICKERING_ANIMATION_DURATION.toInt()
        ).toLong()
        this.isFlickering = typedArray.getBoolean(R.styleable.DigitalTextView_isFlickering, false)
        this.bottomBound = typedArray.getInt(
            R.styleable.DigitalTextView_bottomBound,
            DEFAULT_BOTTOM_BOUND
        )
        this.upperBound = typedArray.getInt(
            R.styleable.DigitalTextView_upperBound,
            DEFAULT_UPPER_BOUND
        )

        require(bottomBound <= upperBound) { "Invalid bounds!" }
        number = bottomBound
        setup(numberTextSize)
        typedArray.recycle()
    }

    var bottomBound: Int
        get() = DEFAULT_BOTTOM_BOUND
        set(value) {
            _bottomBound = value
        }

    var upperBound: Int
        get() = _upperBound
        set(value) {
            _upperBound = value
            _digitCount = value.countDigits()
        }

    var isFlickering: Boolean
        get() = _isFlickering
        set(value) {
            if (!this.isFlickering && value) {
                start()
                return
            }
            if (this.isFlickering && !value) {
                stop()
                return
            }
        }

    var number: Int
        get() = _number
        set(value) {
            _number = when {
                value in bottomBound until upperBound -> value
                value < bottomBound -> bottomBound
                else -> upperBound
            }
            stop()
            setNumberInternal(value)
        }

    var delay: Long
        get() = _flickeringAnimationDuration
        set(value) {
            _flickeringAnimationDuration = Math.min(15000, Math.max(1, value))
        }

    private fun setup(spTextSize: Float) {
        val paint = Paint()
        val typeface = Typeface.createFromAsset(context.assets, DIGITAL_NUMBER_FONT_PATH)
        paint.typeface = typeface
        paint.textSize = DimensionsUtil.getPixelsFromSp(context, spTextSize)

        val textWidth = paint.measureText(String.format(Locale.getDefault(), "%0${upperBound.countDigits()}d", 0))

        with(textView) {
            setTypeface(typeface)
            setTextColor(digitalTextColor)
            textSize = spTextSize
            layoutParams = LayoutParams(textWidth.toInt(), WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
                leftMargin = DimensionsUtil.getPixelsFromDp(context, 4)
                rightMargin = DimensionsUtil.getPixelsFromDp(context, 4)
                topMargin = DimensionsUtil.getPixelsFromDp(context, 4)
                bottomMargin = DimensionsUtil.getPixelsFromDp(context, 4)
            }
        }
        this.addView(textView)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.isFlickering = false
        this._digitalTextHandler.removeCallbacks(runListener)
    }

    private fun start() {
        this.textView.postDelayed(runListener, 0)
        _isFlickering = true
    }

    private fun stop() {
        _digitalTextHandler.removeCallbacks(runListener)
        _isFlickering = false
    }

    private val runListener = object : Runnable {
        override fun run() {
            val value = _randomizer.nextInt(upperBound - bottomBound) + bottomBound
            setNumberInternal(value)
            _digitalTextHandler.postDelayed(this, 50)
        }
    }

    private fun setNumberInternal(number: Int) {
        textView.text = String.format(Locale.getDefault(), "%0${_digitCount}d", number)
    }

}
