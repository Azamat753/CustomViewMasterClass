package com.lawlett.avatar_imageview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF

class AvatarImageViewImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), AvatarImageView {

    companion object {
        const val DEFAULT_BORDER_COLOR = Color.BLACK
        const val DEFAULT_BORDER_WIDTH = 2
        const val DEFAULT_SIZE = 40
    }

    /**
     * size border
     */
    @Px
    var borderWidth: Float = context.convertDpToPx(DEFAULT_BORDER_WIDTH)

    /**
     * color border
     */
    @ColorInt
    private var borderColor: Int = Color.BLACK

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private lateinit var resultBm: Bitmap
    private lateinit var maskBm: Bitmap
    private lateinit var srcBm: Bitmap

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageViewImpl)
            borderWidth = ta.getDimension(
                R.styleable.AvatarImageViewImpl_aiv_borderWidth,
                context.convertDpToPx(DEFAULT_BORDER_WIDTH)
            )

            borderColor = ta.getColor(
                R.styleable.AvatarImageViewImpl_aiv_borderColor,
                DEFAULT_BORDER_COLOR
            )

            ta.recycle()
        }

        scaleType = ScaleType.CENTER
        setUp()
    }

    private fun setUp() {
        with(maskPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }

        with(borderPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
    }

    /**
     * this method return default size
     *
     * @param spec      A MeasureSpec for define current spec
     * @return A Int value to default size view
     */
    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.convertDpToPx(DEFAULT_SIZE).toInt()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(resultBm, viewRect, viewRect, null)
        val half = (borderWidth / 2).toInt()
        viewRect.inset(half, half)
        canvas?.drawOval(viewRect.toRectF(), borderPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(w == 0) return
        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }

        prepareBitmaps(w, h)
    }

    /**
     * this method prepare bitmaps for future use
     * and draw mask with src drawable
     */
    private fun prepareBitmaps(w: Int, h: Int) {
        //prepare buffer this

        maskBm = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        resultBm = maskBm.copy(Bitmap.Config.ARGB_8888, true)
        val maskCanvas = Canvas(maskBm)
        maskCanvas.drawOval(viewRect.toRectF(), maskPaint)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)

        val resultCanvas = Canvas(resultBm)

        resultCanvas.drawBitmap(maskBm, viewRect, viewRect, null)
        resultCanvas.drawBitmap(srcBm, viewRect, viewRect, maskPaint)
    }

    override fun setBorderWidth(border: Int) {
        borderWidth = border.toFloat()
        setUp()
        requestLayout()
        invalidate()
    }

    override fun setBorderColor(color: Int) {
        borderColor = color
        setUp()
        invalidate()
    }

}