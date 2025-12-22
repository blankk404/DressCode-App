package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * 搭配-衣物关联（用于拼图复现位置）
 */
@Entity(tableName = "outfit_item_cross_ref",
        primaryKeys = {"outfit_id", "clothing_id"},
        foreignKeys = {
                @ForeignKey(entity = Outfit.class,
                        parentColumns = "id",
                        childColumns = "outfit_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ClothingItem.class,
                        parentColumns = "id",
                        childColumns = "clothing_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("outfit_id"), @Index("clothing_id")})
public class OutfitItemCrossRef {

    @ColumnInfo(name = "outfit_id")
    private int outfitId;

    @ColumnInfo(name = "clothing_id")
    private int clothingId;

    // 画布相对坐标/缩放（0~1 之间更稳定）
    @ColumnInfo(name = "rel_x")
    private float relX;

    @ColumnInfo(name = "rel_y")
    private float relY;

    @ColumnInfo(name = "scale")
    private float scale;

    @ColumnInfo(name = "rotation")
    private float rotation;

    public OutfitItemCrossRef(int outfitId, int clothingId, float relX, float relY, float scale, float rotation) {
        this.outfitId = outfitId;
        this.clothingId = clothingId;
        this.relX = relX;
        this.relY = relY;
        this.scale = scale;
        this.rotation = rotation;
    }

    public int getOutfitId() {
        return outfitId;
    }

    public void setOutfitId(int outfitId) {
        this.outfitId = outfitId;
    }

    public int getClothingId() {
        return clothingId;
    }

    public void setClothingId(int clothingId) {
        this.clothingId = clothingId;
    }

    public float getRelX() {
        return relX;
    }

    public void setRelX(float relX) {
        this.relX = relX;
    }

    public float getRelY() {
        return relY;
    }

    public void setRelY(float relY) {
        this.relY = relY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}


