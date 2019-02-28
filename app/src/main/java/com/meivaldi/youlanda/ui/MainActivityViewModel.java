package com.meivaldi.youlanda.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.Product;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final ProductRepository mRepository;
    private final LiveData<List<Product>> productList;

    public MainActivityViewModel(ProductRepository repository) {
        mRepository = repository;
        productList = mRepository.getProducts();
    }

    public LiveData<List<Product>> getProductList() {
        return productList;
    }

}
