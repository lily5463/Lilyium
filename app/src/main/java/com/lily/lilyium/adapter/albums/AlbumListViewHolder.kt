package com.lily.lilyium.adapter.albums

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lily.lilyium.databinding.GridItemBinding

//import com.lily.lilyium.databinding.ListItemTransformBinding


class AlbumListViewHolder (binding: GridItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    val imageView: ImageView = binding.imageViewItemTransform
//    val textView: TextView = binding.textViewItemTransform
}