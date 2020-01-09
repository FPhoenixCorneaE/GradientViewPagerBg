package com.wkz.viewpagerbggradient

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    val mBgColor by lazy {
        intArrayOf(
            0xffff8080.toInt(),
            0xffffbc00.toInt(),
            0xff199afe.toInt(),
            0xff00ab96.toInt()
        )
    }

    val mDatas by lazy {
        arrayOf(
            "http://file02.16sucai.com/d/file/2014/0704/e53c868ee9e8e7b28c424b56afe2066d.jpg",
            "http://file02.16sucai.com/d/file/2014/0829/372edfeb74c3119b666237bd4af92be5.jpg",
            "http://file02.16sucai.com/d/file/2014/0807/247d1b1d3fc65f7516856244257f71ec.jpg",
            "http://i2.w.yun.hjfile.cn/doc/201303/54c809bf-1eb2-400b-827f-6f024d7d599b_01.jpg"
        )
    }

    val mViewPager2Adapter: ViewPager2Adapter by lazy(LazyThreadSafetyMode.NONE) {
        ViewPager2Adapter(
            mDatas
        )
    }

    var mSlideshowDisposable: Disposable? = null
    var mCurrentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化ViewPager2
        initViewPager2()

        // 初始化轮播图
        initSlideshowDisposable()
    }

    private fun initViewPager2() {
        mVpPager2.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = mViewPager2Adapter
            offscreenPageLimit = mDatas.size
            setCurrentItem((Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2) % mDatas.size, false)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    Log.i("onPageScrolled", "position-->$position")
                    val startColor = mBgColor[position % mDatas.size]
                    val endColor = when {
                        position % mDatas.size == mBgColor.size - 1 -> {
                            mBgColor.first()
                        }
                        else -> {
                            mBgColor[position % mDatas.size + 1]
                        }
                    }
                    // ARGB求值器
                    val evaluator = ArgbEvaluator()
                    val evaluate = evaluator.evaluate(
                        positionOffset,
                        startColor,
                        endColor
                    ) as Int
                    // 为ViewPager的父容器设置背景色
                    mVpPager2.setBackgroundColor(evaluate)
                }

                override fun onPageSelected(position: Int) {
                    mCurrentPosition = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager2.SCROLL_STATE_DRAGGING -> dispose()
                        else -> initSlideshowDisposable()
                    }
                }
            })
        }
    }

    private fun initSlideshowDisposable() {
        dispose()
        mSlideshowDisposable = Observable.interval(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mVpPager2.setCurrentItem(mCurrentPosition + 1, true)
                },
                {

                })
    }

    private fun dispose() {
        if (mSlideshowDisposable != null && !mSlideshowDisposable!!.isDisposed) {
            mSlideshowDisposable!!.dispose()
        }
    }
}
