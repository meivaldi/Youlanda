package com.meivaldi.youlanda.data.network;

import com.meivaldi.youlanda.data.database.product.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("/android/get_product.php")
    Call<List<Product>> getAllBreads();

}
