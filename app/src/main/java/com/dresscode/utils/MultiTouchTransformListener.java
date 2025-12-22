package com.dresscode.utils;

import android.view.MotionEvent;
import android.view.View;

/**
 * 简易自由变换：拖拽 + 双指缩放 + 双指旋转
 * 使用 View 的 x/y/scaleX/scaleY/rotation 属性实现
 */
public class MultiTouchTransformListener implements View.OnTouchListener {

    private static final int INVALID_POINTER_ID = -1;

    private int ptrId1 = INVALID_POINTER_ID;
    private int ptrId2 = INVALID_POINTER_ID;

    private float lastX;
    private float lastY;

    private float initialDist;
    private float initialRotation;
    private float startScale;
    private float startViewRotation;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                ptrId1 = event.getPointerId(event.getActionIndex());
                lastX = event.getRawX();
                lastY = event.getRawY();
                v.bringToFront();
                return true;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (ptrId2 == INVALID_POINTER_ID) {
                    ptrId2 = event.getPointerId(event.getActionIndex());
                    initialDist = distance(event, ptrId1, ptrId2);
                    initialRotation = rotation(event, ptrId1, ptrId2);
                    startScale = v.getScaleX();
                    startViewRotation = v.getRotation();
                }
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (ptrId1 != INVALID_POINTER_ID && ptrId2 != INVALID_POINTER_ID) {
                    float dist = distance(event, ptrId1, ptrId2);
                    if (initialDist > 10f && dist > 0f) {
                        float scale = startScale * (dist / initialDist);
                        // 限制范围，避免消失
                        scale = Math.max(0.3f, Math.min(3.0f, scale));
                        v.setScaleX(scale);
                        v.setScaleY(scale);
                    }

                    float rot = rotation(event, ptrId1, ptrId2);
                    float delta = rot - initialRotation;
                    v.setRotation(startViewRotation + delta);
                } else {
                    // 单指拖拽
                    float x = event.getRawX();
                    float y = event.getRawY();
                    float dx = x - lastX;
                    float dy = y - lastY;
                    v.setX(v.getX() + dx);
                    v.setY(v.getY() + dy);
                    lastX = x;
                    lastY = y;
                }
                return true;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                int upId = event.getPointerId(event.getActionIndex());
                if (upId == ptrId2) {
                    ptrId2 = INVALID_POINTER_ID;
                } else if (upId == ptrId1) {
                    ptrId1 = ptrId2;
                    ptrId2 = INVALID_POINTER_ID;
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                ptrId1 = INVALID_POINTER_ID;
                ptrId2 = INVALID_POINTER_ID;
                return true;
            }
        }
        return false;
    }

    private float distance(MotionEvent e, int id1, int id2) {
        int i1 = e.findPointerIndex(id1);
        int i2 = e.findPointerIndex(id2);
        if (i1 < 0 || i2 < 0) return 0f;
        float x = e.getX(i1) - e.getX(i2);
        float y = e.getY(i1) - e.getY(i2);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float rotation(MotionEvent e, int id1, int id2) {
        int i1 = e.findPointerIndex(id1);
        int i2 = e.findPointerIndex(id2);
        if (i1 < 0 || i2 < 0) return 0f;
        float dx = e.getX(i2) - e.getX(i1);
        float dy = e.getY(i2) - e.getY(i1);
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
}


