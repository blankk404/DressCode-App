package com.dresscode.data.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

/**
 * 衣物及其详细信息（包含类别和风格）
 */
public class ClothingWithDetails {
    
    @Embedded
    public ClothingItem clothingItem;

    @Relation(
            parentColumn = "category_id",
            entityColumn = "id"
    )
    public Category category;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = ClothingStyleCrossRef.class,
                    parentColumn = "clothing_id",
                    entityColumn = "style_id"
            )
    )
    public List<Style> styles;
}

