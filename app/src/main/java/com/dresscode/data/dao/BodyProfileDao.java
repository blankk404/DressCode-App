package com.dresscode.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dresscode.data.entity.BodyProfile;

/**
 * 身材档案数据访问对象（DAO）
 */
@Dao
public interface BodyProfileDao {

    /**
     * 插入身材档案
     */
    @Insert
    long insertBodyProfile(BodyProfile bodyProfile);

    /**
     * 更新身材档案
     */
    @Update
    int updateBodyProfile(BodyProfile bodyProfile);

    /**
     * 删除身材档案
     */
    @Delete
    void deleteBodyProfile(BodyProfile bodyProfile);

    /**
     * 根据用户ID查询身材档案
     */
    @Query("SELECT * FROM body_profiles WHERE user_id = :userId LIMIT 1")
    BodyProfile getBodyProfileByUserId(int userId);

    /**
     * 根据用户ID查询身材档案（LiveData）
     */
    @Query("SELECT * FROM body_profiles WHERE user_id = :userId LIMIT 1")
    LiveData<BodyProfile> getBodyProfileByUserIdLive(int userId);

    /**
     * 根据ID查询身材档案
     */
    @Query("SELECT * FROM body_profiles WHERE id = :id LIMIT 1")
    BodyProfile getBodyProfileById(int id);

    /**
     * 删除指定用户的身材档案
     */
    @Query("DELETE FROM body_profiles WHERE user_id = :userId")
    void deleteBodyProfileByUserId(int userId);
}

