package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 衣物类别实体
 */
@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name; // 类别名称（如：上衣、外套、裤子、裙子、鞋子、配饰）

    @ColumnInfo(name = "icon")
    private String icon; // 图标资源名

    public Category() {
    }

    public Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

