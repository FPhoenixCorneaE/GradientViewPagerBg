package com.fphoenixcorneae.viewpagerbggradient

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.fphoenixcorneae.palette.PaletteKtx
import com.fphoenixcorneae.palette.Swatch
import com.fphoenixcorneae.viewpagerbggradient.databinding.ItemViewPager2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewPager2Adapter(
    val mDatas: Array<Any?>
) : RecyclerView.Adapter<ViewPager2ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ViewHolder {
        return ViewPager2ViewHolder(
            ItemViewPager2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return when (mDatas.size) {
            0 -> 0
            1 -> 1
            else -> Int.MAX_VALUE
        }
    }

    override fun onBindViewHolder(holder: ViewPager2ViewHolder, position: Int) {
        holder.viewBinding.apply {
            val context = mIvItem.context
            val imgData = mDatas[position % mDatas.size]

            CoroutineScope(Dispatchers.IO).launch {
                val request = ImageRequest.Builder(context)
                    .apply {
                        transformations(RoundedCornersTransformation(16f))
                    }
                    .data(imgData)
                    .build()
                val drawable = context.imageLoader.execute(request).drawable
                drawable?.let {
                    withContext(Dispatchers.Main) {
                        mIvItem.setImageBitmap(it.toBitmap())
                        PaletteKtx.getInstance()
                            .data(imgData)
                            .use(Swatch.Dominant)
                            .intoBackground(mClItem, Swatch.Color.Argb)
                            .start(it.toBitmap().copy(Bitmap.Config.ARGB_8888, true))
                    }
                }
            }
        }
    }
}

class ViewPager2ViewHolder(
    val viewBinding: ItemViewPager2Binding
) : RecyclerView.ViewHolder(viewBinding.root)