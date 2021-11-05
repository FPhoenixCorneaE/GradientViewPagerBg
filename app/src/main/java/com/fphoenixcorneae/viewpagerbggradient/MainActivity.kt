package com.fphoenixcorneae.viewpagerbggradient

import android.animation.ArgbEvaluator
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.viewpager2.widget.ViewPager2
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.fphoenixcorneae.palette.PaletteKtx
import com.fphoenixcorneae.palette.Swatch
import com.fphoenixcorneae.viewpagerbggradient.databinding.ActivityMainBinding
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private var mViewBinding: ActivityMainBinding? = null
    private val mBgColor by lazy {
        IntArray(4)
    }

    private val mDatas: Array<Any?> by lazy {
        arrayOf(
            R.mipmap.pic_1,
            R.mipmap.pic_2,
            R.mipmap.pic_3,
            R.mipmap.pic_4,
        )
    }

    private val mViewPager2Adapter: ViewPager2Adapter by lazy(LazyThreadSafetyMode.NONE) {
        ViewPager2Adapter(
            mDatas
        )
    }

    private var mSlideshowJob: Job? = null
    private var mCurrentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding!!.root)

        mDatas.forEachIndexed { index, any ->
            CoroutineScope(Dispatchers.IO).launch {
                val request = ImageRequest.Builder(this@MainActivity)
                    .apply {
                        transformations(RoundedCornersTransformation(16f))
                    }
                    .data(any)
                    .build()
                val drawable = this@MainActivity.imageLoader.execute(request).drawable
                drawable?.let {
                    PaletteKtx.getInstance()
                        .use(Swatch.Dominant)
                        .intoCallback {
                            it?.dominantSwatch?.let {
                                mBgColor[index] = it.rgb
                            }
                        }
                        .start(it.toBitmap().copy(Bitmap.Config.ARGB_8888, true))
                }
            }
        }

        // 初始化ViewPager2
        initViewPager2()

        // 初始化轮播图
        initSlideshowJob()
    }

    private fun initViewPager2() {
        mViewBinding?.apply {
            vpPager2.apply {
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                adapter = mViewPager2Adapter
                offscreenPageLimit = mDatas.size
                mCurrentPosition = (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2) % mDatas.size
                setCurrentItem(mCurrentPosition, false)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        Log.i("OnPageChangeCallback", "onPageScrolled: position-->$position")
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
                        vpPager2.setBackgroundColor(evaluate)
                        rlTitle.setBackgroundColor(evaluate)
                        immersionBar {
                            statusBarColorInt(evaluate)
                        }
                    }

                    override fun onPageSelected(position: Int) {
                        Log.i("OnPageChangeCallback", "onPageSelected: position-->$position")
                        mCurrentPosition = position
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        Log.i("OnPageChangeCallback", "onPageScrollStateChanged: state-->$state")
                        when (state) {
                            ViewPager2.SCROLL_STATE_DRAGGING -> cancel()
                            else -> initSlideshowJob()
                        }
                    }
                })
            }
        }
    }

    private fun initSlideshowJob() {
        cancel()
        mSlideshowJob = CoroutineScope(Dispatchers.Main).launch {
            repeat(Int.MAX_VALUE) {
                delay(3000)
                mViewBinding?.vpPager2?.setCurrentItem(mCurrentPosition + 1, 800)
            }
        }
    }

    private fun cancel() {
        mSlideshowJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding = null
    }
}
