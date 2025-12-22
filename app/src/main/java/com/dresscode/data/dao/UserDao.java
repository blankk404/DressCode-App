package com.dresscode.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dresscode.data.entity.User;

import java.util.List;

/**
 * 用户数据访问对象（DAO）
 */
@Dao
public interface UserDao {

    /**
     * 插入用户
     */
    @Insert
    long insertUser(User user);

    /**
     * 更新用户
     */
    @Update
    int updateUser(User user);

    /**
     * 删除用户
     */
    @Delete
    void deleteUser(User user);

    /**
     * 根据用户名查询用户
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    User getUserByPhone(String phone);

    /**
     * 根据用户名和密码查询用户（登录验证）
     */
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    /**
     * 根据手机号和密码查询用户（手机号登录验证）
     */
    @Query("SELECT * FROM users WHERE phone = :phone AND password = :password LIMIT 1")
    User loginByPhone(String phone, String password);

    /**
     * 根据ID查询用户
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    LiveData<User> getUserById(int userId);

    /**
     * 查询所有用户
     */
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    /**
     * 删除所有用户
     */
    @Query("DELETE FROM users")
    void deleteAllUsers();
}

