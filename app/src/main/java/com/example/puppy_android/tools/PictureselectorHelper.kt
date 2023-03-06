package com.example.puppy_android.tools

import android.app.Activity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.coroutines.flow.*


class PicturePicker(activity: Activity): OnResultCallbackListener<LocalMedia> {
    private val selector = PictureSelector.create(activity)

    private val _flow = MutableSharedFlow<MutableList<LocalMedia>?>(extraBufferCapacity = 1)

    val flow: Flow<MutableList<LocalMedia>?>
        get() = _flow.asSharedFlow()

    fun launch() {
        selector
            .openGallery(PictureMimeType.ofImage())
            .imageEngine(GlideEngine)
            .forResult(this)
    }

    override fun onResult(result: MutableList<LocalMedia>?) {
        _flow.tryEmit(result)
    }

    override fun onCancel() {
    }
}