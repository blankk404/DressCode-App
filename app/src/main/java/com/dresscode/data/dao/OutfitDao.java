package com.dresscode.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.dresscode.data.entity.Outfit;
import com.dresscode.data.entity.OutfitItemCrossRef;
import com.dresscode.data.entity.OutfitItemWithClothing;

import java.util.List;

@Dao
public interface OutfitDao {

    @Insert
    long insertOutfit(Outfit outfit);

    @Insert
    void insertOutfitItems(List<OutfitItemCrossRef> items);

    @Query("DELETE FROM outfit_item_cross_ref WHERE outfit_id = :outfitId")
    void deleteOutfitItems(int outfitId);

    @Query("UPDATE outfits SET wear_photo_uri = :wearPhotoUri WHERE id = :outfitId")
    void updateWearPhoto(int outfitId, String wearPhotoUri);

    @Query("DELETE FROM outfits WHERE id = :outfitId")
    void deleteOutfit(int outfitId);

    @Query("SELECT COUNT(*) FROM outfits WHERE user_id = :userId")
    LiveData<Integer> getOutfitCount(int userId);

    @Query("SELECT * FROM outfits WHERE user_id = :userId AND (:scene IS NULL OR scene = :scene) AND (:season IS NULL OR season = :season) ORDER BY created_at DESC")
    LiveData<List<Outfit>> getOutfits(int userId, String scene, String season);

    @Query("SELECT * FROM outfits WHERE id = :outfitId LIMIT 1")
    LiveData<Outfit> getOutfitById(int outfitId);

    @Query("SELECT * FROM outfit_item_cross_ref WHERE outfit_id = :outfitId")
    LiveData<List<OutfitItemCrossRef>> getOutfitItems(int outfitId);

    @Transaction
    @Query("SELECT * FROM outfit_item_cross_ref WHERE outfit_id = :outfitId")
    LiveData<List<OutfitItemWithClothing>> getOutfitItemsWithClothing(int outfitId);

    @Query("UPDATE outfits SET collage_path = :collagePath, scene = COALESCE(:scene, scene), season = COALESCE(:season, season) WHERE id = :outfitId")
    void updateOutfitMeta(int outfitId, String collagePath, String scene, String season);
}


