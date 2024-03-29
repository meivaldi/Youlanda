package com.meivaldi.youlanda.data.database.product;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ProductDAO {

    @Insert
    void insertAllProducts(List<Product> productList);

    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("DELETE FROM product")
    void deleteAllProducts();

    @Query("SELECT * FROM product WHERE jenis = :jenis")
    LiveData<List<Product>> getAllProducts(String jenis);

    @Query("SELECT * FROM product WHERE nama = :name")
    Product getProduct(String name);

}
