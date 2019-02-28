package com.meivaldi.youlanda.data.network;

import com.meivaldi.youlanda.data.database.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetDataService {

    @GET("/android/get_product.php?jenis=roti")
    Call<List<Product>> getAllBreads();

    @GET("/android/get_product.php?jenis=tar")
    Call<List<Product>> getAllTarts();

    @GET("/android/get_product.php?jenis=bolu")
    Call<List<Product>> getAllSponges();
}
