package com.meivaldi.youlanda.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final ProductRepository mRepository;
    private final LiveData<List<Product>> productList;

    public MainActivityViewModel(ProductRepository repository, String jenis) {
        mRepository = repository;
        productList = mRepository.getProducts(jenis);
    }

    public LiveData<List<Product>> getProductList() {
        return productList;
    }

}
