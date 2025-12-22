package com.dresscode.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dresscode.data.entity.InspirationPhoto;
import com.dresscode.data.entity.InspirationTag;
import com.dresscode.data.repository.InspirationRepository;

import java.util.List;

public class InspirationViewModel extends AndroidViewModel {

    private final InspirationRepository repo;
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public InspirationViewModel(@NonNull Application application) {
        super(application);
        repo = new InspirationRepository(application);
    }

    public LiveData<List<InspirationTag>> getTags(int userId) {
        return repo.getTags(userId);
    }

    public LiveData<List<InspirationPhoto>> getPhotosByTag(int tagId) {
        return repo.getPhotosByTag(tagId);
    }

    public void addTag(int userId, String name) {
        repo.addTag(new InspirationTag(userId, name), new InspirationRepository.OnSimpleListener() {
            @Override
            public void onSuccess() {
                message.postValue("已添加标签");
            }

            @Override
            public void onFailed(String messageStr) {
                message.postValue("添加失败：" + messageStr);
            }
        });
    }

    public void updateTag(InspirationTag tag, String name) {
        tag.setName(name);
        repo.updateTag(tag, new InspirationRepository.OnSimpleListener() {
            @Override
            public void onSuccess() {
                message.postValue("已更新标签");
            }

            @Override
            public void onFailed(String messageStr) {
                message.postValue("更新失败：" + messageStr);
            }
        });
    }

    public void deleteTag(InspirationTag tag) {
        repo.deleteTag(tag, new InspirationRepository.OnSimpleListener() {
            @Override
            public void onSuccess() {
                message.postValue("已删除标签");
            }

            @Override
            public void onFailed(String messageStr) {
                message.postValue("删除失败：" + messageStr);
            }
        });
    }

    public void addPhoto(int tagId, String uri) {
        repo.addPhoto(new InspirationPhoto(tagId, uri), new InspirationRepository.OnSimpleListener() {
            @Override
            public void onSuccess() {
                message.postValue("已添加图片");
            }

            @Override
            public void onFailed(String messageStr) {
                message.postValue("添加失败：" + messageStr);
            }
        });
    }

    public void deletePhoto(InspirationPhoto photo) {
        repo.deletePhoto(photo, new InspirationRepository.OnSimpleListener() {
            @Override
            public void onSuccess() {
                message.postValue("已删除图片");
            }

            @Override
            public void onFailed(String messageStr) {
                message.postValue("删除失败：" + messageStr);
            }
        });
    }

    public LiveData<String> getMessage() {
        return message;
    }
}


