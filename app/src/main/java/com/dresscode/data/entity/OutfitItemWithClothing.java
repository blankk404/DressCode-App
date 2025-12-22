package com.dresscode.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * 搭配中的单品 + 位置信息
 */
public class OutfitItemWithClothing {
    @Embedded
    public OutfitItemCrossRef ref;

    @Relation(parentColumn = "clothing_id", entityColumn = "id")
    public ClothingItem clothingItem;
}


