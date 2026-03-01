package com.hisilicon.dlna.dmc.processor.impl;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.hisilicon.dlna.dmc.processor.interfaces.ImageCallback;

/* loaded from: classes.dex */
public class ImageCallbackImpl implements ImageCallback {
    private String id;
    private ImageView mImageView;

    public ImageCallbackImpl(ImageView imageView) {
        this.mImageView = imageView;
    }

    public ImageCallbackImpl(ImageView imageView, String id) {
        this.mImageView = imageView;
        this.id = id;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.ImageCallback
    public void imageLoaded(Bitmap bitmap, String imageLink) {
        if (this.mImageView != null && bitmap != null && !bitmap.isRecycled() && imageLink != null && imageLink.equals(this.mImageView.getTag())) {
            this.mImageView.setImageBitmap(bitmap);
        }
    }
}
