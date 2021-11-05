package com.fphoenixcorneae.viewpagerbggradient

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.viewpager2.widget.ViewPager2

/**
 * ViewPager2 改变动画切换速度
 * 设置当前Item
 * @param item         下一个跳转的item
 * @param duration     scroll时长
 * @param interpolator 插值器
 * @param pagePxWidth  页面宽度,单位"px"
 */
fun ViewPager2.setCurrentItem(
    item: Int,
    duration: Long,
    interpolator: TimeInterpolator = LinearInterpolator(),
    pagePxWidth: Int = width // 使用viewpager2.getWidth()获取
) {
    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            beginFakeDrag()
        }

        override fun onAnimationEnd(animation: Animator?) {
            endFakeDrag()
        }

        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {}
    })
    animator.interpolator = interpolator
    animator.duration = duration
    animator.start()
}