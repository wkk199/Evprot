package com.evport.businessapp.ui.page.adapter

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.databinding.ItemAddImgBinding
import com.evport.businessapp.utils.loader.GlideMy.ImageLoaderOptions

class ImageAddAdapter(val context: Context) :
    SimpleDataBindingAdapter<Uri, ItemAddImgBinding>(
        context,
        R.layout.item_add_img,
        object : DiffUtil.ItemCallback<Uri>() {
            override fun areItemsTheSame(
                oldItem: Uri,
                newItem: Uri
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Uri,
                newItem: Uri
            ): Boolean {
                return oldItem == newItem
            }
        }) {


    val optionRequest by lazy {
        ImageLoaderOptions.builder()
            .placeholder(R.color.light_gray)
            .error(R.color.light_gray)
//                .glideRound(mContext.dip(18), GlideRoundedCornersTransform.CornerType.TOP)
            .build()
    }

    val add = Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.resources.getResourcePackageName(R.drawable.image_add) + "/"
                + context.resources.getResourceTypeName(R.drawable.image_add) + "/"
                + context.resources.getResourceEntryName(R.drawable.image_add)
    )

    var delClick: ((item: Uri) -> Any)? = null
    override fun onBindItem(
        binding: ItemAddImgBinding,
        item: Uri,
        holder: RecyclerView.ViewHolder
    ) {
        if (item == add) {
            binding.rlImageAdd.background =context.resources.getDrawable(R.drawable.shape_image_add_bt)
            binding.imgUriBg.visibility =View.VISIBLE
            binding.imgUri.setImageURI(null)
        }else{
            binding.imgUri.setImageURI(item)
            binding.imgUriBg.visibility =View.GONE
            binding.rlImageAdd.background =context.resources.getDrawable(R.drawable.shape_image_add_bt)
        }
        binding.imgDelete.isVisible = item != add

        binding.imgDelete.setOnClickListener {
            delClick?.invoke(item)
        }
    }


}