package com.dresscode.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dresscode.data.entity.InspirationPhoto;
import com.dresscode.data.entity.InspirationTag;

import java.util.List;

@Dao
public interface InspirationDao {

    // Tag
    @Insert
    long insertTag(InspirationTag tag);

    @Update
    int updateTag(InspirationTag tag);

    @Delete
    void deleteTag(InspirationTag tag);

    @Query("SELECT * FROM inspiration_tags WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<InspirationTag>> getTags(int userId);

    // Photo
    @Insert
    long insertPhoto(InspirationPhoto photo);

    @Delete
    void deletePhoto(InspirationPhoto photo);

    @Query("SELECT * FROM inspiration_photos WHERE tag_id = :tagId ORDER BY created_at DESC")
    LiveData<List<InspirationPhoto>> getPhotosByTag(int tagId);
}


