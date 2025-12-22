package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 搭配实体
 */
@Entity(tableName = "outfits",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id"), @Index("scene"), @Index("season")})
public class Outfit {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "scene")
    private String scene; // 工作/休闲/运动/约会/旅行

    @ColumnInfo(name = "season")
    private String season; // 春/夏/秋/冬/四季

    @ColumnInfo(name = "collage_path")
    private String collagePath; // 拼图图片（本地文件路径）

    @ColumnInfo(name = "wear_photo_uri")
    private String wearPhotoUri; // 上身照（content uri string）

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public Outfit() {
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getCollagePath() {
        return collagePath;
    }

    public void setCollagePath(String collagePath) {
        this.collagePath = collagePath;
    }

    public String getWearPhotoUri() {
        return wearPhotoUri;
    }

    public void setWearPhotoUri(String wearPhotoUri) {
        this.wearPhotoUri = wearPhotoUri;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}


