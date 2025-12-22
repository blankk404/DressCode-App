package com.dresscode.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dresscode.data.entity.BodyProfile;
import com.dresscode.data.repository.BodyProfileRepository;

/**
 * 身材档案ViewModel
 */
public class BodyProfileViewModel extends AndroidViewModel {

    private BodyProfileRepository bodyProfileRepository;
    private MutableLiveData<String> saveMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public BodyProfileViewModel(@NonNull Application application) {
        super(application);
        bodyProfileRepository = new BodyProfileRepository(application);
    }

    /**
     * 保存身材档案
     */
    public void saveBodyProfile(BodyProfile bodyProfile) {
        isLoading.setValue(true);
        bodyProfileRepository.saveBodyProfile(bodyProfile, new BodyProfileRepository.OnSaveListener() {
            @Override
            public void onSaveSuccess(String message) {
                isLoading.postValue(false);
                saveMessage.postValue(message);
            }

            @Override
            public void onSaveFailed(String message) {
                isLoading.postValue(false);
                saveMessage.postValue(message);
            }
        });
    }

    /**
     * 根据用户ID获取身材档案
     */
    public LiveData<BodyProfile> getBodyProfileByUserId(int userId) {
        return bodyProfileRepository.getBodyProfileByUserId(userId);
    }

    /**
     * 删除身材档案
     */
    public void deleteBodyProfile(int userId) {
        isLoading.setValue(true);
        bodyProfileRepository.deleteBodyProfile(userId, new BodyProfileRepository.OnDeleteListener() {
            @Override
            public void onDeleteSuccess() {
                isLoading.postValue(false);
                saveMessage.postValue("删除成功");
            }

            @Override
            public void onDeleteFailed(String message) {
                isLoading.postValue(false);
                saveMessage.postValue(message);
            }
        });
    }

    // Getter方法
    public LiveData<String> getSaveMessage() {
        return saveMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}

