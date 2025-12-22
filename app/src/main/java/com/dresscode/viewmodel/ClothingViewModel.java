package com.dresscode.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dresscode.data.entity.Category;
import com.dresscode.data.entity.ClothingItem;
import com.dresscode.data.entity.ClothingWithDetails;
import com.dresscode.data.entity.Style;
import com.dresscode.data.repository.ClothingRepository;

import java.util.List;

/**
 * 衣物ViewModel
 */
public class ClothingViewModel extends AndroidViewModel {

    private ClothingRepository clothingRepository;
    private MutableLiveData<String> operationMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ClothingViewModel(@NonNull Application application) {
        super(application);
        clothingRepository = new ClothingRepository(application);
    }

    // ===== ClothingItem操作 =====
    
    public void insertClothingItem(ClothingItem item, List<Integer> styleIds) {
        isLoading.setValue(true);
        clothingRepository.insertClothingItem(item, styleIds, new ClothingRepository.OnSaveListener() {
            @Override
            public void onSaveSuccess(String message) {
                isLoading.postValue(false);
                operationMessage.postValue(message);
            }

            @Override
            public void onSaveFailed(String message) {
                isLoading.postValue(false);
                operationMessage.postValue(message);
            }
        });
    }

    public void updateClothingItem(ClothingItem item, List<Integer> styleIds) {
        isLoading.setValue(true);
        clothingRepository.updateClothingItem(item, styleIds, new ClothingRepository.OnSaveListener() {
            @Override
            public void onSaveSuccess(String message) {
                isLoading.postValue(false);
                operationMessage.postValue(message);
            }

            @Override
            public void onSaveFailed(String message) {
                isLoading.postValue(false);
                operationMessage.postValue(message);
            }
        });
    }

    public void deleteClothingItem(ClothingItem item) {
        isLoading.setValue(true);
        clothingRepository.deleteClothingItem(item, new ClothingRepository.OnDeleteListener() {
            @Override
            public void onDeleteSuccess() {
                isLoading.postValue(false);
                operationMessage.postValue("删除成功");
            }

            @Override
            public void onDeleteFailed(String message) {
                isLoading.postValue(false);
                operationMessage.postValue(message);
            }
        });
    }

    public LiveData<List<ClothingItem>> getClothingItemsByUserId(int userId) {
        return clothingRepository.getClothingItemsByUserId(userId);
    }

    public LiveData<List<ClothingItem>> getClothingItemsByCategory(int userId, int categoryId) {
        return clothingRepository.getClothingItemsByCategory(userId, categoryId);
    }

    public LiveData<List<ClothingItem>> getFavoriteClothingItems(int userId) {
        return clothingRepository.getFavoriteClothingItems(userId);
    }

    public LiveData<ClothingItem> getClothingItemById(int id) {
        return clothingRepository.getClothingItemById(id);
    }

    public LiveData<ClothingWithDetails> getClothingWithDetails(int id) {
        return clothingRepository.getClothingWithDetails(id);
    }

    public LiveData<List<ClothingWithDetails>> getAllClothingWithDetails(int userId) {
        return clothingRepository.getAllClothingWithDetails(userId);
    }

    // ===== Category和Style =====
    
    public LiveData<List<Category>> getAllCategories() {
        return clothingRepository.getAllCategories();
    }

    public LiveData<List<Style>> getAllStyles() {
        return clothingRepository.getAllStyles();
    }

    public LiveData<List<Style>> getStylesForClothing(int clothingId) {
        return clothingRepository.getStylesForClothing(clothingId);
    }

    // Getter方法
    public LiveData<String> getOperationMessage() {
        return operationMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}

