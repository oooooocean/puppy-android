package com.example.puppy_android.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView

object GlideEngine: ImageEngine {
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView?,
        callback: OnImageCompleteCallback?
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    callback?.onShowLoading()
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    callback?.onHideLoading()
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback?.onHideLoading()
                    val isLongImage = MediaUtils.isLongImg(resource.width, resource.height)
                    longImageView?.visibility = if (isLongImage) View.VISIBLE else View.GONE
                    imageView.visibility = if (isLongImage) View.GONE else View.VISIBLE
                    if (!isLongImage) imageView.setImageBitmap(resource)
                    longImageView?.isQuickScaleEnabled = true
                    longImageView?.isZoomEnabled = true
                    longImageView?.setDoubleTapZoomDuration(100)
                    longImageView?.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                    longImageView?.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                    longImageView?.setImage(ImageSource.cachedBitmap(resource), ImageViewState(0F, PointF(0F, 0F), 0))
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .centerCrop()
            .sizeMultiplier(0.5f)
            .placeholder(android.R.drawable.ic_menu_camera)
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    val drawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                    drawable.cornerRadius = 8F
                    imageView.setImageDrawable(drawable)
                }
            })
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_camera)
            .into(imageView)
    }
}