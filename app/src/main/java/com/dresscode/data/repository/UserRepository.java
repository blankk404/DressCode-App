package com.dresscode.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dresscode.data.dao.UserDao;
import com.dresscode.data.database.AppDatabase;
import com.dresscode.data.entity.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用户数据仓库
 */
public class UserRepository {

    private UserDao userDao;
    private ExecutorService executorService;

    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 注册用户
     */
    public void registerUser(User user, OnRegisterListener listener) {
        executorService.execute(() -> {
            try {
                // 检查用户名是否已存在
                User existingUser = userDao.getUserByUsername(user.getUsername());
                if (existingUser != null) {
                    listener.onRegisterFailed("用户名已存在");
                    return;
                }

                // 检查手机号是否已存在
                User existingPhone = userDao.getUserByPhone(user.getPhone());
                if (existingPhone != null) {
                    listener.onRegisterFailed("手机号已注册");
                    return;
                }

                // 插入用户
                long id = userDao.insertUser(user);
                if (id > 0) {
                    user.setId((int) id);
                    listener.onRegisterSuccess(user);
                } else {
                    listener.onRegisterFailed("注册失败，请重试");
                }
            } catch (Exception e) {
                listener.onRegisterFailed("注册失败：" + e.getMessage());
            }
        });
    }

    /**
     * 用户登录
     */
    public void loginUser(String account, String password, OnLoginListener listener) {
        executorService.execute(() -> {
            try {
                User user;
                // 判断是手机号还是用户名
                if (account.matches("^1[3-9]\\d{9}$")) {
                    // 手机号登录
                    user = userDao.loginByPhone(account, password);
                } else {
                    // 用户名登录
                    user = userDao.login(account, password);
                }

                if (user != null) {
                    listener.onLoginSuccess(user);
                } else {
                    listener.onLoginFailed("账号或密码错误");
                }
            } catch (Exception e) {
                listener.onLoginFailed("登录失败：" + e.getMessage());
            }
        });
    }

    /**
     * 更新用户信息
     */
    public void updateUser(User user, OnUpdateListener listener) {
        executorService.execute(() -> {
            try {
                int result = userDao.updateUser(user);
                if (result > 0) {
                    listener.onUpdateSuccess();
                } else {
                    listener.onUpdateFailed("更新失败");
                }
            } catch (Exception e) {
                listener.onUpdateFailed("更新失败：" + e.getMessage());
            }
        });
    }

    /**
     * 根据ID获取用户
     */
    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    /**
     * 获取所有用户
     */
    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    // 回调接口
    public interface OnRegisterListener {
        void onRegisterSuccess(User user);
        void onRegisterFailed(String message);
    }

    public interface OnLoginListener {
        void onLoginSuccess(User user);
        void onLoginFailed(String message);
    }

    public interface OnUpdateListener {
        void onUpdateSuccess();
        void onUpdateFailed(String message);
    }
}

