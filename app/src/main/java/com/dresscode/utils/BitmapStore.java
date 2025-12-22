package com.dresscode.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 保存拼图到 app 内部存储，返回文件路径（长期可用，不依赖外部权限）
 */
public class BitmapStore {

    public static String saveOutfitCollage(Context context, Bitmap bitmap) throws Exception {
        File dir = new File(context.getFilesDir(), "outfits");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "outfit_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        }
        return file.getAbsolutePath();
    }
}


