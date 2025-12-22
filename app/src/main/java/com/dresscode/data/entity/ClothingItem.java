package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 衣物实体
 */
@Entity(tableName = "clothing_items",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index("user_id"), @Index("category_id")})
public class ClothingItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId; // 用户ID

    @ColumnInfo(name = "category_id")
    private int categoryId; // 类别ID

    @ColumnInfo(name = "name")
    private String name; // 衣物名称

    @ColumnInfo(name = "brand")
    private String brand; // 品牌

    @ColumnInfo(name = "color")
    private String color; // 颜色

    @ColumnInfo(name = "size")
    private String size; // 尺码

    @ColumnInfo(name = "season")
    private String season; // 季节（春、夏、秋、冬、四季）

    @ColumnInfo(name = "image_path")
    private String imagePath; // 图片路径

    @ColumnInfo(name = "purchase_date")
    private long purchaseDate; // 购买日期

    @ColumnInfo(name = "price")
    private float price; // 价格

    @ColumnInfo(name = "notes")
    private String notes; // 备注

    @ColumnInfo(name = "favorite")
    private boolean favorite; // 是否收藏

    @ColumnInfo(name = "created_at")
    private long createdAt; // 创建时间

    public ClothingItem() {
        this.createdAt = System.currentTimeMillis();
    }

    // Getter 和 Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

