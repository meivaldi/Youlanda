package com.meivaldi.youlanda.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {Product.class}, version = 1)
public abstract class ProductDatabase extends RoomDatabase {

    private static final String TAG = ProductDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "product";

    private static final Object LOCK = new Object();
    private static ProductDatabase instance;

    public static ProductDatabase getInstance(Context context) {
        Log.d(TAG, "Getting database");
        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        ProductDatabase.class, ProductDatabase.DATABASE_NAME).build();
                Log.d(TAG, "New Database Created");
            }
        }

        return instance;
    }

    public abstract ProductDAO productDAO();
}
