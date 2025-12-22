package com.dresscode.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dresscode.data.dao.ClothingDao;
import com.dresscode.data.database.AppDatabase;
import com.dresscode.data.entity.Category;
import com.dresscode.data.entity.ClothingItem;
import com.dresscode.data.entity.ClothingStyleCrossRef;
import com.dresscode.data.entity.ClothingWithDetails;
import com.dresscode.data.entity.Style;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 衣物数据仓库
 */
public class ClothingRepository {

    private ClothingDao clothingDao;
    private ExecutorService executorService;

    public ClothingRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        clothingDao = database.clothingDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // ===== ClothingItem操作 =====
    
    public void insertClothingItem(ClothingItem item, List<Integer> styleIds, OnSaveListener listener) {
        executorService.execute(() -> {
            try {
                long id = clothingDao.insertClothingItem(item);
                if (id > 0) {
                    item.setId((int) id);
                    // 插入风格关联
                    if (styleIds != null) {
                        for (int styleId : styleIds) {
                            clothingDao.insertClothingStyleCrossRef(
                                new ClothingStyleCrossRef((int) id, styleId)
                            );
                        }
                    }
                    listener.onSaveSuccess("添加成功");
                } else {
                    listener.onSaveFailed("添加失败");
                }
            } catch (Exception e) {
                listener.onSaveFailed("添加失败：" + e.getMessage());
            }
        });
    }

    public void updateClothingItem(ClothingItem item, List<Integer> styleIds, OnSaveListener listener) {
        executorService.execute(() -> {
            try {
                int result = clothingDao.updateClothingItem(item);
                if (result > 0) {
                    // 更新风格关联
                    clothingDao.deleteAllStylesForClothing(item.getId());
                    if (styleIds != null) {
                        for (int styleId : styleIds) {
                            clothingDao.insertClothingStyleCrossRef(
                                new ClothingStyleCrossRef(item.getId(), styleId)
                            );
                        }
                    }
                    listener.onSaveSuccess("更新成功");
                } else {
                    listener.onSaveFailed("更新失败");
                }
            } catch (Exception e) {
                listener.onSaveFailed("更新失败：" + e.getMessage());
            }
        });
    }

    public void deleteClothingItem(ClothingItem item, OnDeleteListener listener) {
        executorService.execute(() -> {
            try {
                clothingDao.deleteClothingItem(item);
                listener.onDeleteSuccess();
            } catch (Exception e) {
                listener.onDeleteFailed("删除失败：" + e.getMessage());
            }
        });
    }

    public LiveData<List<ClothingItem>> getClothingItemsByUserId(int userId) {
        return clothingDao.getClothingItemsByUserId(userId);
    }

    public LiveData<List<ClothingItem>> getClothingItemsByCategory(int userId, int categoryId) {
        return clothingDao.getClothingItemsByCategory(userId, categoryId);
    }

    public LiveData<List<ClothingItem>> getFavoriteClothingItems(int userId) {
        return clothingDao.getFavoriteClothingItems(userId);
    }

    public LiveData<ClothingItem> getClothingItemById(int id) {
        return clothingDao.getClothingItemByIdLive(id);
    }

    public LiveData<ClothingWithDetails> getClothingWithDetails(int id) {
        return clothingDao.getClothingWithDetails(id);
    }

    public LiveData<List<ClothingWithDetails>> getAllClothingWithDetails(int userId) {
        return clothingDao.getAllClothingWithDetails(userId);
    }

    // ===== Category操作 =====
    
    public LiveData<List<Category>> getAllCategories() {
        return clothingDao.getAllCategories();
    }

    // ===== Style操作 =====
    
    public LiveData<List<Style>> getAllStyles() {
        return clothingDao.getAllStyles();
    }

    public LiveData<List<Style>> getStylesForClothing(int clothingId) {
        return clothingDao.getStylesForClothing(clothingId);
    }

    // 回调接口
    public interface OnSaveListener {
        void onSaveSuccess(String message);
        void onSaveFailed(String message);
    }

    public interface OnDeleteListener {
        void onDeleteSuccess();
        void onDeleteFailed(String message);
    }
}

