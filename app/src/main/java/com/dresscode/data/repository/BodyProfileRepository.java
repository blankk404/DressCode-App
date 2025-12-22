package com.dresscode.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dresscode.data.dao.BodyProfileDao;
import com.dresscode.data.database.AppDatabase;
import com.dresscode.data.entity.BodyProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 身材档案数据仓库
 */
public class BodyProfileRepository {

    private BodyProfileDao bodyProfileDao;
    private ExecutorService executorService;

    public BodyProfileRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        bodyProfileDao = database.bodyProfileDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 保存或更新身材档案
     */
    public void saveBodyProfile(BodyProfile bodyProfile, OnSaveListener listener) {
        executorService.execute(() -> {
            try {
                // 检查用户是否已有身材档案
                BodyProfile existing = bodyProfileDao.getBodyProfileByUserId(bodyProfile.getUserId());
                
                if (existing != null) {
                    // 更新现有档案
                    bodyProfile.setId(existing.getId());
                    bodyProfile.setUpdatedAt(System.currentTimeMillis());
                    int result = bodyProfileDao.updateBodyProfile(bodyProfile);
                    if (result > 0) {
                        listener.onSaveSuccess("身材档案更新成功");
                    } else {
                        listener.onSaveFailed("更新失败");
                    }
                } else {
                    // 创建新档案
                    bodyProfile.setUpdatedAt(System.currentTimeMillis());
                    long id = bodyProfileDao.insertBodyProfile(bodyProfile);
                    if (id > 0) {
                        bodyProfile.setId((int) id);
                        listener.onSaveSuccess("身材档案保存成功");
                    } else {
                        listener.onSaveFailed("保存失败");
                    }
                }
            } catch (Exception e) {
                listener.onSaveFailed("操作失败：" + e.getMessage());
            }
        });
    }

    /**
     * 根据用户ID获取身材档案
     */
    public LiveData<BodyProfile> getBodyProfileByUserId(int userId) {
        return bodyProfileDao.getBodyProfileByUserIdLive(userId);
    }

    /**
     * 根据用户ID同步获取身材档案
     */
    public void getBodyProfileSync(int userId, OnGetListener listener) {
        executorService.execute(() -> {
            try {
                BodyProfile bodyProfile = bodyProfileDao.getBodyProfileByUserId(userId);
                listener.onGetSuccess(bodyProfile);
            } catch (Exception e) {
                listener.onGetFailed("获取失败：" + e.getMessage());
            }
        });
    }

    /**
     * 删除身材档案
     */
    public void deleteBodyProfile(int userId, OnDeleteListener listener) {
        executorService.execute(() -> {
            try {
                bodyProfileDao.deleteBodyProfileByUserId(userId);
                listener.onDeleteSuccess();
            } catch (Exception e) {
                listener.onDeleteFailed("删除失败：" + e.getMessage());
            }
        });
    }

    // 回调接口
    public interface OnSaveListener {
        void onSaveSuccess(String message);
        void onSaveFailed(String message);
    }

    public interface OnGetListener {
        void onGetSuccess(BodyProfile bodyProfile);
        void onGetFailed(String message);
    }

    public interface OnDeleteListener {
        void onDeleteSuccess();
        void onDeleteFailed(String message);
    }
}

