package com.wkz.viewpagerbggradient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import kotlinx.android.synthetic.main.item_view_pager2.view.*

class ViewPager2Adapter(val mDatas: Array<String>) : RecyclerView.Adapter<ViewPager2ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ViewHolder {
        return ViewPager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_pager2,
                parent,
                false
            )
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
        Glide.with(holder.itemView.mIvItem)
            .load(mDatas[position % mDatas.size])
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
            .listener(
                GlidePalette.with(mDatas[position % mDatas.size])
                    .use(BitmapPalette.Profile.MUTED)
                    .intoBackground(holder.itemView.mClItem, BitmapPalette.Swatch.RGB)
                    .intoCallBack {
                        val swatch = it?.mutedSwatch
                        swatch?.rgb
                    }
            )
            .into(holder.itemView.mIvItem)
    }
}

class ViewPager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}