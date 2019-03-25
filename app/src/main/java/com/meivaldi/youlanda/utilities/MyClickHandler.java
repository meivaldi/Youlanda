package com.meivaldi.youlanda.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.data.network.GetDataService;
import com.meivaldi.youlanda.data.network.RetrofitClientInstance;
import com.meivaldi.youlanda.ui.CheckoutActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyClickHandler {

    private static Context context;

    public MyClickHandler(Context context) {
        this.context = context;
    }

    public static void purchase(View view) {
        /*Intent intent = new Intent(context, CheckoutActivity.class);
        context.startActivity(intent);*/

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Product> call = service.saveProduct(20);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                Toast.makeText(context, "Berhasil", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(context, "Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

