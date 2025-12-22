package com.dresscode.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 头像选择辅助类
 */
public class AvatarHelper {

    public static final int REQUEST_CODE_CHOOSE_IMAGE = 1001;
    public static final int REQUEST_CODE_TAKE_PHOTO = 1002;

    /**
     * 从相册选择图片
     */
    public static void chooseImageFromGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
    }

    /**
     * 拍照
     */
    public static void takePhoto(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    /**
     * 显示选择对话框
     */
    public static void showChooseDialog(Activity activity, OnChooseListener listener) {
        // 这里可以扩展为显示选择对话框（相册/拍照/取消）
        // 目前简化为直接从相册选择
        chooseImageFromGallery(activity);
    }

    /**
     * 获取图片URI的真实路径（简化版）
     * 注意：完整实现需要处理不同Android版本的兼容性
     */
    public static String getImagePath(Activity activity, Uri uri) {
        // 简化实现，返回URI字符串
        // 实际应用中需要使用ContentResolver获取真实路径
        return uri.toString();
    }

    public interface OnChooseListener {
        void onChooseFromGallery();
        void onTakePhoto();
        void onCancel();
    }
}

