package com.dresscode.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dresscode.data.entity.Outfit;
import com.dresscode.data.entity.OutfitItemCrossRef;
import com.dresscode.data.entity.OutfitItemWithClothing;
import com.dresscode.data.repository.OutfitRepository;

import java.util.List;

public class OutfitViewModel extends AndroidViewModel {

    private final OutfitRepository repository;
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> savedOutfitId = new MutableLiveData<>();

    public OutfitViewModel(@NonNull Application application) {
        super(application);
        repository = new OutfitRepository(application);
    }

    public LiveData<List<Outfit>> getOutfits(int userId, String scene, String season) {
        return repository.getOutfits(userId, scene, season);
    }

    public LiveData<Integer> getOutfitCount(int userId) {
        return repository.getOutfitCount(userId);
    }

    public LiveData<Outfit> getOutfitById(int outfitId) {
        return repository.getOutfitById(outfitId);
    }

    public LiveData<List<OutfitItemWithClothing>> getOutfitItemsWithClothing(int outfitId) {
        return repository.getOutfitItemsWithClothing(outfitId);
    }

    public void saveOutfit(Outfit outfit, List<OutfitItemCrossRef> items) {
        loading.setValue(true);
        repository.saveOutfit(outfit, items, new OutfitRepository.OnSaveListener() {
            @Override
            public void onSuccess(int outfitId) {
                loading.postValue(false);
                savedOutfitId.postValue(outfitId);
                message.postValue("保存成功");
            }

            @Override
            public void onFailed(String messageStr) {
                loading.postValue(false);
                message.postValue(messageStr);
            }
        });
    }

    public void updateWearPhoto(int outfitId, String uri) {
        repository.updateWearPhoto(outfitId, uri, new OutfitRepository.OnSimpleListener() {
            @Override
            public void onSuccess() {
                message.postValue("已上传上身照");
            }

            @Override
            public void onFailed(String messageStr) {
                message.postValue(messageStr);
            }
        });
    }

    public void updateOutfit(int outfitId, String collagePath, String scene, String season, List<OutfitItemCrossRef> items) {
        loading.setValue(true);
        repository.updateOutfit(outfitId, collagePath, scene, season, items, new OutfitRepository.OnSimpleListener() {
            @Override
            public void onSuccess() {
                loading.postValue(false);
                message.postValue("保存成功");
            }

            @Override
            public void onFailed(String messageStr) {
                loading.postValue(false);
                message.postValue(messageStr);
            }
        });
    }

    public void deleteOutfit(int outfitId) {
        repository.deleteOutfit(outfitId, new OutfitRepository.OnSimpleListener() {
            @Override
            public void onSuccess() {
                message.postValue("已删除搭配");
            }

            @Override
            public void onFailed(String messageStr) {
                message.postValue(messageStr);
            }
        });
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<Integer> getSavedOutfitId() {
        return savedOutfitId;
    }
}


