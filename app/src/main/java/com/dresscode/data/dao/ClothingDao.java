package com.dresscode.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.dresscode.data.entity.Category;
import com.dresscode.data.entity.ClothingItem;
import com.dresscode.data.entity.ClothingStyleCrossRef;
import com.dresscode.data.entity.ClothingWithDetails;
import com.dresscode.data.entity.Style;

import java.util.List;

/**
 * 衣物数据访问对象
 */
@Dao
public interface ClothingDao {

    // ===== Category =====
    @Insert
    long insertCategory(Category category);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(int id);

    @Query("SELECT COUNT(*) FROM categories")
    int countCategories();

    // ===== Style =====
    @Insert
    long insertStyle(Style style);

    @Query("SELECT * FROM styles")
    LiveData<List<Style>> getAllStyles();

    @Query("SELECT * FROM styles WHERE id = :id")
    Style getStyleById(int id);

    @Query("SELECT COUNT(*) FROM styles")
    int countStyles();

    // ===== ClothingItem =====
    @Insert
    long insertClothingItem(ClothingItem clothingItem);

    @Update
    int updateClothingItem(ClothingItem clothingItem);

    @Delete
    void deleteClothingItem(ClothingItem clothingItem);

    @Query("SELECT * FROM clothing_items WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<ClothingItem>> getClothingItemsByUserId(int userId);

    @Query("SELECT * FROM clothing_items WHERE user_id = :userId AND category_id = :categoryId ORDER BY created_at DESC")
    LiveData<List<ClothingItem>> getClothingItemsByCategory(int userId, int categoryId);

    @Query("SELECT * FROM clothing_items WHERE user_id = :userId AND favorite = 1 ORDER BY created_at DESC")
    LiveData<List<ClothingItem>> getFavoriteClothingItems(int userId);

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    ClothingItem getClothingItemById(int id);

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    LiveData<ClothingItem> getClothingItemByIdLive(int id);

    // ===== ClothingStyleCrossRef =====
    @Insert
    void insertClothingStyleCrossRef(ClothingStyleCrossRef crossRef);

    @Delete
    void deleteClothingStyleCrossRef(ClothingStyleCrossRef crossRef);

    @Query("DELETE FROM clothing_style_cross_ref WHERE clothing_id = :clothingId")
    void deleteAllStylesForClothing(int clothingId);

    @Query("SELECT * FROM styles WHERE id IN (SELECT style_id FROM clothing_style_cross_ref WHERE clothing_id = :clothingId)")
    LiveData<List<Style>> getStylesForClothing(int clothingId);

    // ===== 复杂查询 =====
    @Transaction
    @Query("SELECT * FROM clothing_items WHERE id = :clothingId")
    LiveData<ClothingWithDetails> getClothingWithDetails(int clothingId);

    @Transaction
    @Query("SELECT * FROM clothing_items WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<ClothingWithDetails>> getAllClothingWithDetails(int userId);
}

