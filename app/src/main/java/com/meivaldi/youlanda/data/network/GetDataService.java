package com.meivaldi.youlanda.data.network;

import com.meivaldi.youlanda.data.database.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetDataService {

    @POST("/android/get_product.php")
    Call<List<Product>> getAllProducts(@Body String jenis);

}
