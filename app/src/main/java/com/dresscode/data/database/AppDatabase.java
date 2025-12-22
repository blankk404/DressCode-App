package com.dresscode.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dresscode.data.dao.BodyProfileDao;
import com.dresscode.data.dao.ClothingDao;
import com.dresscode.data.dao.InspirationDao;
import com.dresscode.data.dao.OutfitDao;
import com.dresscode.data.dao.UserDao;
import com.dresscode.data.entity.BodyProfile;
import com.dresscode.data.entity.Category;
import com.dresscode.data.entity.ClothingItem;
import com.dresscode.data.entity.ClothingStyleCrossRef;
import com.dresscode.data.entity.Style;
import com.dresscode.data.entity.User;
import com.dresscode.data.entity.Outfit;
import com.dresscode.data.entity.OutfitItemCrossRef;
import com.dresscode.data.entity.InspirationTag;
import com.dresscode.data.entity.InspirationPhoto;

import java.util.concurrent.Executors;

/**
 * 应用数据库
 */
@Database(entities = {
        User.class, 
        BodyProfile.class,
        Category.class,
        Style.class,
        ClothingItem.class,
        ClothingStyleCrossRef.class,
        Outfit.class,
        OutfitItemCrossRef.class,
        InspirationTag.class,
        InspirationPhoto.class
}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();
    
    public abstract BodyProfileDao bodyProfileDao();
    
    public abstract ClothingDao clothingDao();
    
    public abstract OutfitDao outfitDao();

    public abstract InspirationDao inspirationDao();

    /**
     * 获取数据库单例
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "dresscode_database")
                    .fallbackToDestructiveMigration() // 版本更新时重建数据库
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            seedIfNeededAsync();
                        }

                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                            // 兜底：如果首次启动时用户很快进入添加衣物，确保种子数据已写入
                            seedIfNeededAsync();
                        }
                    })
                    .build();
        }
        return instance;
    }

    private static void seedIfNeededAsync() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (instance != null) {
                initializeData(instance);
            }
        });
    }

    /**
     * 初始化基础数据
     */
    private static void initializeData(AppDatabase database) {
        ClothingDao dao = database.clothingDao();

        // 仅当表为空时插入（避免重复/避免外键 race）
        if (dao.countCategories() == 0) {
            dao.insertCategory(new Category("上衣", "ic_top"));
            dao.insertCategory(new Category("外套", "ic_coat"));
            dao.insertCategory(new Category("裤子", "ic_pants"));
            dao.insertCategory(new Category("裙子", "ic_skirt"));
            dao.insertCategory(new Category("鞋子", "ic_shoes"));
            dao.insertCategory(new Category("配饰", "ic_accessory"));
        }

        if (dao.countStyles() == 0) {
            dao.insertStyle(new Style("休闲", "#4CAF50"));
            dao.insertStyle(new Style("商务", "#2196F3"));
            dao.insertStyle(new Style("运动", "#FF9800"));
            dao.insertStyle(new Style("街头", "#9C27B0"));
            dao.insertStyle(new Style("复古", "#795548"));
            dao.insertStyle(new Style("甜美", "#E91E63"));
        }
    }
}

