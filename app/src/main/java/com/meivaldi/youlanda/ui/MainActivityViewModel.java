package com.meivaldi.youlanda.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.Product;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final ProductRepository repository;
    private final LiveData<List<Product>> productList;

    public MainActivityViewModel(ProductRepository repository) {
        this.repository = repository;
        productList = repository.getProducts("roti");
    }

    public LiveData<List<Product>> getProductList() {
        return productList;
    }

}
