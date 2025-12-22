package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 用户实体类
 */
@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "username")
    private String username; // 用户名

    @ColumnInfo(name = "password")
    private String password; // 密码

    @ColumnInfo(name = "phone")
    private String phone; // 手机号

    @ColumnInfo(name = "nickname")
    private String nickname; // 昵称

    @ColumnInfo(name = "gender")
    private String gender; // 性别 (男/女/保密)

    @ColumnInfo(name = "avatar")
    private String avatar; // 头像URL或路径

    @ColumnInfo(name = "created_at")
    private long createdAt; // 创建时间

    // 构造函数
    public User() {
    }

    @Ignore
    public User(String username, String password, String phone, String nickname, String gender, String avatar) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.nickname = nickname;
        this.gender = gender;
        this.avatar = avatar;
        this.createdAt = System.currentTimeMillis();
    }

    // Getter 和 Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", avatar='" + avatar + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

