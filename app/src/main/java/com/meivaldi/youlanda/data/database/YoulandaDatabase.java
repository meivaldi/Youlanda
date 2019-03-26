package com.meivaldi.youlanda.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.data.database.product.ProductDAO;

@Database(entities = {Product.class}, version = 1)
public abstract class YoulandaDatabase extends RoomDatabase {

    private static final String TAG = YoulandaDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "product";

    private static final Object LOCK = new Object();
    private static YoulandaDatabase instance;

    public static YoulandaDatabase getInstance(Context context) {
        Log.d(TAG, "Getting database");
        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        YoulandaDatabase.class, YoulandaDatabase.DATABASE_NAME).build();
                Log.d(TAG, "New Database Created");
            }
        }

        return instance;
    }

    public abstract ProductDAO productDAO();
}
