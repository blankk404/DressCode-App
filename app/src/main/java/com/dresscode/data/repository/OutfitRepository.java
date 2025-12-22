package com.dresscode.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dresscode.data.dao.OutfitDao;
import com.dresscode.data.database.AppDatabase;
import com.dresscode.data.entity.Outfit;
import com.dresscode.data.entity.OutfitItemCrossRef;
import com.dresscode.data.entity.OutfitItemWithClothing;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OutfitRepository {

    private final OutfitDao outfitDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public OutfitRepository(Application application) {
        outfitDao = AppDatabase.getInstance(application).outfitDao();
    }

    public LiveData<List<Outfit>> getOutfits(int userId, String scene, String season) {
        return outfitDao.getOutfits(userId, scene, season);
    }

    public LiveData<Integer> getOutfitCount(int userId) {
        return outfitDao.getOutfitCount(userId);
    }

    public LiveData<Outfit> getOutfitById(int outfitId) {
        return outfitDao.getOutfitById(outfitId);
    }

    public LiveData<List<OutfitItemWithClothing>> getOutfitItemsWithClothing(int outfitId) {
        return outfitDao.getOutfitItemsWithClothing(outfitId);
    }

    public void saveOutfit(Outfit outfit, List<OutfitItemCrossRef> items, OnSaveListener listener) {
        executor.execute(() -> {
            try {
                long id = outfitDao.insertOutfit(outfit);
                if (id <= 0) {
                    listener.onFailed("保存失败");
                    return;
                }
                int outfitId = (int) id;
                if (items != null && !items.isEmpty()) {
                    for (OutfitItemCrossRef ref : items) {
                        ref.setOutfitId(outfitId);
                    }
                    outfitDao.insertOutfitItems(items);
                }
                listener.onSuccess(outfitId);
            } catch (Exception e) {
                listener.onFailed("保存失败：" + e.getMessage());
            }
        });
    }

    public void updateWearPhoto(int outfitId, String uri, OnSimpleListener listener) {
        executor.execute(() -> {
            try {
                outfitDao.updateWearPhoto(outfitId, uri);
                listener.onSuccess();
            } catch (Exception e) {
                listener.onFailed("更新失败：" + e.getMessage());
            }
        });
    }

    public void updateOutfit(int outfitId, String collagePath, String scene, String season, List<OutfitItemCrossRef> items, OnSimpleListener listener) {
        executor.execute(() -> {
            try {
                outfitDao.updateOutfitMeta(outfitId, collagePath, scene, season);
                outfitDao.deleteOutfitItems(outfitId);
                if (items != null && !items.isEmpty()) {
                    for (OutfitItemCrossRef ref : items) {
                        ref.setOutfitId(outfitId);
                    }
                    outfitDao.insertOutfitItems(items);
                }
                listener.onSuccess();
            } catch (Exception e) {
                listener.onFailed("保存失败：" + e.getMessage());
            }
        });
    }

    public void deleteOutfit(int outfitId, OnSimpleListener listener) {
        executor.execute(() -> {
            try {
                outfitDao.deleteOutfitItems(outfitId);
                outfitDao.deleteOutfit(outfitId);
                listener.onSuccess();
            } catch (Exception e) {
                listener.onFailed("删除失败：" + e.getMessage());
            }
        });
    }

    public interface OnSaveListener {
        void onSuccess(int outfitId);
        void onFailed(String message);
    }

    public interface OnSimpleListener {
        void onSuccess();
        void onFailed(String message);
    }
}


