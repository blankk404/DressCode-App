package com.dresscode.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dresscode.data.entity.User;
import com.dresscode.data.repository.UserRepository;

import java.util.List;

/**
 * 用户ViewModel
 */
public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private MutableLiveData<String> registerMessage = new MutableLiveData<>();
    private MutableLiveData<String> loginMessage = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    /**
     * 注册用户
     */
    public void registerUser(String username, String password, String phone, String nickname, String gender) {
        isLoading.setValue(true);
        User user = new User(username, password, phone, nickname, gender, "");
        userRepository.registerUser(user, new UserRepository.OnRegisterListener() {
            @Override
            public void onRegisterSuccess(User user) {
                isLoading.postValue(false);
                registerMessage.postValue("注册成功");
                currentUser.postValue(user);
            }

            @Override
            public void onRegisterFailed(String message) {
                isLoading.postValue(false);
                registerMessage.postValue(message);
            }
        });
    }

    /**
     * 用户登录
     */
    public void loginUser(String account, String password) {
        isLoading.setValue(true);
        userRepository.loginUser(account, password, new UserRepository.OnLoginListener() {
            @Override
            public void onLoginSuccess(User user) {
                isLoading.postValue(false);
                loginMessage.postValue("登录成功");
                currentUser.postValue(user);
            }

            @Override
            public void onLoginFailed(String message) {
                isLoading.postValue(false);
                loginMessage.postValue(message);
            }
        });
    }

    /**
     * 更新用户信息
     */
    public void updateUser(User user) {
        isLoading.setValue(true);
        userRepository.updateUser(user, new UserRepository.OnUpdateListener() {
            @Override
            public void onUpdateSuccess() {
                isLoading.postValue(false);
                currentUser.postValue(user);
            }

            @Override
            public void onUpdateFailed(String message) {
                isLoading.postValue(false);
            }
        });
    }

    /**
     * 根据ID获取用户
     */
    public LiveData<User> getUserById(int userId) {
        return userRepository.getUserById(userId);
    }

    /**
     * 获取所有用户
     */
    public LiveData<List<User>> getAllUsers() {
        return userRepository.getAllUsers();
    }

    // Getter方法
    public LiveData<String> getRegisterMessage() {
        return registerMessage;
    }

    public LiveData<String> getLoginMessage() {
        return loginMessage;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * 清空当前用户（登出）
     */
    public void logout() {
        currentUser.setValue(null);
    }
}

