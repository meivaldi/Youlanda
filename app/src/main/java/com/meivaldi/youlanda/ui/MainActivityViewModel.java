package com.meivaldi.youlanda.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final ProductRepository mRepository;
    private final LiveData<List<Product>> productList;
    private MutableLiveData<List<Order>> mOrderList;

    public MainActivityViewModel(ProductRepository repository, String jenis) {
        mRepository = repository;
        productList = mRepository.getProducts(jenis);
        mOrderList = new MutableLiveData<>();
    }

    public LiveData<List<Product>> getProductList() {
        return productList;
    }

    public MutableLiveData<List<Order>> getOrderList() {
        return mOrderList;
    }

    public void setOrderList(List<Order> orderList) {
        mOrderList.postValue(orderList);
    }
}
