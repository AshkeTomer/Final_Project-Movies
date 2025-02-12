package com.example.midproject_imdb.ui.favorite_movies

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatImageButton

class FavoriteButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    fun animateClick() {
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(this@FavoriteButton, "scaleX", 1f, 0.8f),
                ObjectAnimator.ofFloat(this@FavoriteButton, "scaleY", 1f, 0.8f)
            )
            duration = 100
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(this@FavoriteButton, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(this@FavoriteButton, "scaleY", 0.8f, 1f)
            )
            duration = 200
            interpolator = OvershootInterpolator()
        }

        AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            start()
        }
    }
}
