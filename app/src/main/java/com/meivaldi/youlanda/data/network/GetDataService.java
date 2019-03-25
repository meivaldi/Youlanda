package com.meivaldi.youlanda.data.network;

import android.support.design.animation.Positioning;

import com.meivaldi.youlanda.data.database.product.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetDataService {

    @GET("/android/get_product.php")
    Call<List<Product>> getAllBreads();

    @POST("/android/store_product.php")
    @FormUrlEncoded
    Call<Product> saveProduct(@Field("stok") int stok);

}
