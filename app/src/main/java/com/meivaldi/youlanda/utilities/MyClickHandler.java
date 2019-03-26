package com.meivaldi.youlanda.utilities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.data.network.GetDataService;
import com.meivaldi.youlanda.data.network.RetrofitClientInstance;
import com.meivaldi.youlanda.ui.CheckoutActivity;
import com.meivaldi.youlanda.ui.MainActivityViewModel;
import com.meivaldi.youlanda.ui.MainViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyClickHandler {

    private Context context;

    public MyClickHandler(Context context) {
        this.context = context;
    }

    public void onPurchaseClicked(Order order) {
        List<Cart> cartList = order.getCartList();

        ProductRepository repository = InjectorUtils.provideRepository(context);
        List<Product> selectedProduct = new ArrayList<>();

        for (Cart cart: cartList) {
            Product product = cart.getProduct();
            product.setSelected(false);

            repository.updateProduct(product);
            selectedProduct.add(product);
        }

        order.getCartList().clear();
        order.setCartSum(0);
        order.setDiskon();
        order.setTotal();
        order.setTax();
        order.setPrice();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Product> call;

        for (int i=0; i<selectedProduct.size(); i++) {
            Product selected = selectedProduct.get(i);
            Log.d("TES", selected.getStok() + " " + selected.getId());
            call = service.saveProduct(Integer.valueOf(selected.getStok()), selected.getId());

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

}

