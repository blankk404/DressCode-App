package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 灵感相册图片（归属某个标签）
 */
@Entity(tableName = "inspiration_photos",
        foreignKeys = @ForeignKey(entity = InspirationTag.class,
                parentColumns = "id",
                childColumns = "tag_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("tag_id")})
public class InspirationPhoto {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "tag_id")
    private int tagId;

    @ColumnInfo(name = "uri")
    private String uri; // content:// 持久化 URI

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public InspirationPhoto() {
        this.createdAt = System.currentTimeMillis();
    }

    public InspirationPhoto(int tagId, String uri) {
        this.tagId = tagId;
        this.uri = uri;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}


