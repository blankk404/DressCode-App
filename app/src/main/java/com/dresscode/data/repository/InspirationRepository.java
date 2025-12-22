package com.dresscode.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dresscode.data.dao.InspirationDao;
import com.dresscode.data.database.AppDatabase;
import com.dresscode.data.entity.InspirationPhoto;
import com.dresscode.data.entity.InspirationTag;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InspirationRepository {

    private final InspirationDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public InspirationRepository(Application app) {
        dao = AppDatabase.getInstance(app).inspirationDao();
    }

    public LiveData<List<InspirationTag>> getTags(int userId) {
        return dao.getTags(userId);
    }

    public LiveData<List<InspirationPhoto>> getPhotosByTag(int tagId) {
        return dao.getPhotosByTag(tagId);
    }

    public void addTag(InspirationTag tag, OnSimpleListener l) {
        executor.execute(() -> {
            try {
                dao.insertTag(tag);
                l.onSuccess();
            } catch (Exception e) {
                l.onFailed(e.getMessage());
            }
        });
    }

    public void updateTag(InspirationTag tag, OnSimpleListener l) {
        executor.execute(() -> {
            try {
                dao.updateTag(tag);
                l.onSuccess();
            } catch (Exception e) {
                l.onFailed(e.getMessage());
            }
        });
    }

    public void deleteTag(InspirationTag tag, OnSimpleListener l) {
        executor.execute(() -> {
            try {
                dao.deleteTag(tag);
                l.onSuccess();
            } catch (Exception e) {
                l.onFailed(e.getMessage());
            }
        });
    }

    public void addPhoto(InspirationPhoto photo, OnSimpleListener l) {
        executor.execute(() -> {
            try {
                dao.insertPhoto(photo);
                l.onSuccess();
            } catch (Exception e) {
                l.onFailed(e.getMessage());
            }
        });
    }

    public void deletePhoto(InspirationPhoto photo, OnSimpleListener l) {
        executor.execute(() -> {
            try {
                dao.deletePhoto(photo);
                l.onSuccess();
            } catch (Exception e) {
                l.onFailed(e.getMessage());
            }
        });
    }

    public interface OnSimpleListener {
        void onSuccess();
        void onFailed(String message);
    }
}


