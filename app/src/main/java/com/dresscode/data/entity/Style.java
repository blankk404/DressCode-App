package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 风格实体
 */
@Entity(tableName = "styles")
public class Style {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name; // 风格名称（如：休闲、商务、运动、街头、复古、甜美）

    @ColumnInfo(name = "color")
    private String color; // 标签颜色

    public Style() {
    }

    public Style(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Getter 和 Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

