package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * 衣物-风格关联表（多对多）
 */
@Entity(tableName = "clothing_style_cross_ref",
        primaryKeys = {"clothing_id", "style_id"},
        foreignKeys = {
                @ForeignKey(entity = ClothingItem.class,
                        parentColumns = "id",
                        childColumns = "clothing_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Style.class,
                        parentColumns = "id",
                        childColumns = "style_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("clothing_id"), @Index("style_id")})
public class ClothingStyleCrossRef {

    @ColumnInfo(name = "clothing_id")
    private int clothingId;

    @ColumnInfo(name = "style_id")
    private int styleId;

    public ClothingStyleCrossRef(int clothingId, int styleId) {
        this.clothingId = clothingId;
        this.styleId = styleId;
    }

    // Getter 和 Setter
    public int getClothingId() {
        return clothingId;
    }

    public void setClothingId(int clothingId) {
        this.clothingId = clothingId;
    }

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }
}

