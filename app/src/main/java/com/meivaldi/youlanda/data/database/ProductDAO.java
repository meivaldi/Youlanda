package com.meivaldi.youlanda.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface ProductDAO {

    @Insert
    void insertAllProducts(Product... products);

    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("SELECT * FROM product WHERE jenis = :jenis")
    void getAllProducts(String jenis);

}
