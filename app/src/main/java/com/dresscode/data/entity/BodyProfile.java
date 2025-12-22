package com.dresscode.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 身材档案实体类
 */
@Entity(tableName = "body_profiles",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class BodyProfile {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId; // 用户ID（外键）

    @ColumnInfo(name = "height")
    private float height; // 身高（cm）

    @ColumnInfo(name = "weight")
    private float weight; // 体重（kg）

    @ColumnInfo(name = "bmi")
    private float bmi; // BMI指数

    @ColumnInfo(name = "head_circumference")
    private float headCircumference; // 头围（cm）

    @ColumnInfo(name = "shoulder_width")
    private float shoulderWidth; // 肩宽（cm）

    @ColumnInfo(name = "waist_circumference")
    private float waistCircumference; // 腰围（cm）

    @ColumnInfo(name = "chest_circumference")
    private float chestCircumference; // 胸围（cm）

    @ColumnInfo(name = "hip_circumference")
    private float hipCircumference; // 臀围（cm）

    @ColumnInfo(name = "shoe_size_eu")
    private float shoeSizeEu; // 鞋码（欧码）

    @ColumnInfo(name = "updated_at")
    private long updatedAt; // 更新时间

    // 构造函数
    public BodyProfile() {
    }

    @androidx.room.Ignore
    public BodyProfile(int userId, float height, float weight) {
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.bmi = calculateBMI(height, weight);
        this.updatedAt = System.currentTimeMillis();
    }

    /**
     * 计算BMI
     * BMI = 体重(kg) / (身高(m) * 身高(m))
     */
    public static float calculateBMI(float height, float weight) {
        if (height <= 0 || weight <= 0) {
            return 0;
        }
        float heightInMeters = height / 100.0f;
        return weight / (heightInMeters * heightInMeters);
    }

    /**
     * 获取BMI状态描述
     */
    public String getBMIStatus() {
        if (bmi < 18.5) {
            return "偏瘦";
        } else if (bmi < 24) {
            return "正常";
        } else if (bmi < 28) {
            return "偏胖";
        } else {
            return "肥胖";
        }
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

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
        updateBMI();
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
        updateBMI();
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getHeadCircumference() {
        return headCircumference;
    }

    public void setHeadCircumference(float headCircumference) {
        this.headCircumference = headCircumference;
    }

    public float getShoulderWidth() {
        return shoulderWidth;
    }

    public void setShoulderWidth(float shoulderWidth) {
        this.shoulderWidth = shoulderWidth;
    }

    public float getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(float waistCircumference) {
        this.waistCircumference = waistCircumference;
    }

    public float getChestCircumference() {
        return chestCircumference;
    }

    public void setChestCircumference(float chestCircumference) {
        this.chestCircumference = chestCircumference;
    }

    public float getHipCircumference() {
        return hipCircumference;
    }

    public void setHipCircumference(float hipCircumference) {
        this.hipCircumference = hipCircumference;
    }

    public float getShoeSizeEu() {
        return shoeSizeEu;
    }

    public void setShoeSizeEu(float shoeSizeEu) {
        this.shoeSizeEu = shoeSizeEu;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 更新BMI
     */
    private void updateBMI() {
        this.bmi = calculateBMI(this.height, this.weight);
    }

    @Override
    public String toString() {
        return "BodyProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", height=" + height +
                ", weight=" + weight +
                ", bmi=" + bmi +
                ", headCircumference=" + headCircumference +
                ", shoulderWidth=" + shoulderWidth +
                ", waistCircumference=" + waistCircumference +
                ", chestCircumference=" + chestCircumference +
                ", hipCircumference=" + hipCircumference +
                ", shoeSizeEu=" + shoeSizeEu +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

