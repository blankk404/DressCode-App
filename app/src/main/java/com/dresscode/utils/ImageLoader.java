package com.dresscode.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.File;

/**
 * 简单图片加载：支持 content:// URI 和 本地文件路径
 */
public class ImageLoader {

    public static void load(ImageView imageView, String pathOrUri, int placeholderRes) {
        if (TextUtils.isEmpty(pathOrUri)) {
            imageView.setImageResource(placeholderRes);
            return;
        }
        try {
            if (pathOrUri.startsWith("content://")) {
                imageView.setImageURI(Uri.parse(pathOrUri));
            } else if (pathOrUri.startsWith("file://")) {
                imageView.setImageURI(Uri.parse(pathOrUri));
            } else {
                File f = new File(pathOrUri);
                imageView.setImageURI(Uri.fromFile(f));
            }
        } catch (Exception e) {
            imageView.setImageResource(placeholderRes);
        }
    }
}


