package showmethe.github.core.glide

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewPropertyAnimator
import androidx.palette.graphics.Palette

import com.bumptech.glide.request.transition.Transition

/**
 * showmethe.github.core.glide
 *
 *
 * 2019/10/11
 */
class DrawableScaleFadeTransition(
    private val duration: Int,
    private val isCrossFadeEnabled: Boolean,
    private val isReveal:Boolean = false
) : Transition<Drawable> {


    override fun transition(current: Drawable, adapter: Transition.ViewAdapter): Boolean {
        var previous = adapter.currentDrawable
        if (previous == null) {
            previous = ColorDrawable( Color.TRANSPARENT)
        }
        val view = adapter.view
        if (view.visibility == View.VISIBLE && view.isAttachedToWindow ) {
            val transitionDrawable = TransitionDrawable(arrayOf(previous, current))
            transitionDrawable.isCrossFadeEnabled = isCrossFadeEnabled
            transitionDrawable.startTransition(duration)
            adapter.setDrawable(transitionDrawable)

            if(isReveal){
                val maxRadius = view.height.coerceAtLeast(view.width)
                val animation = ViewAnimationUtils.createCircularReveal(
                    view,
                    transitionDrawable.intrinsicWidth / 2,
                    transitionDrawable.intrinsicHeight / 2,
                    0f,
                    maxRadius.toFloat()
                )
                animation.duration = duration.toLong()
                animation.start()
            }
            view.scaleX = 1.2f
            view.scaleY = 1.2f
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(duration.toLong()).start()
        }else{
            view.scaleX = 1.0f
            view.scaleY = 1.0f
        }
        return true
    }

}