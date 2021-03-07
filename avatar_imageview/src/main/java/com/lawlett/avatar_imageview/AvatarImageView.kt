package com.lawlett.avatar_imageview

import androidx.annotation.ColorInt

interface AvatarImageView {
    fun setBorderWidth(border: Int)
    fun setBorderColor(@ColorInt color: Int)
}