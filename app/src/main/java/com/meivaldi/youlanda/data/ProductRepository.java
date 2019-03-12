package com.meivaldi.youlanda.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.meivaldi.youlanda.AppExecutors;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.data.database.product.ProductDAO;
import com.meivaldi.youlanda.data.network.ProductNetworkDataSource;

import java.util.List;

public class ProductRepository {
    private static final String TAG = ProductRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static ProductRepository instance;

    private final ProductDAO productDAO;
    private final ProductNetworkDataSource productNetworkDataSource;
    private final AppExecutors appExecutors;

    private boolean initialized = false;
    private Product temp;

    public ProductRepository(ProductDAO productDAO,
                             ProductNetworkDataSource productNetworkDataSource,
                             AppExecutors appExecutors) {
        this.productDAO = productDAO;
        this.productNetworkDataSource = productNetworkDataSource;
        this.appExecutors = appExecutors;

        LiveData<List<Product>> networkData = productNetworkDataSource.getCurrentProducts();
        networkData.observeForever(newProductFromNetwork -> {
            appExecutors.diskIO().execute(() -> {
                deleteOldData();
                Log.d(TAG, "Delete old weather");

                productDAO.insertAllProducts(newProductFromNetwork);
                Log.d(TAG, "Insert new values");
            });
        });
    }

    public static synchronized ProductRepository getInstance(
            ProductDAO productDAO, ProductNetworkDataSource productNetworkDataSource,
            AppExecutors appExecutors) {
        Log.d(TAG, "Getting repository");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new ProductRepository(productDAO, productNetworkDataSource, appExecutors);
                Log.d(TAG, "New repository created");
            }
        }

        return instance;
    }

    private synchronized void initializeData() {
        if (initialized) return;
        initialized = true;

        productNetworkDataSource.scheduleRecurringFetchProductSync();

        appExecutors.diskIO().execute(() -> {
            if (isFetchedNeeded()) {
                startFetchProductService();
            }
        });
    }

    private boolean isFetchedNeeded() {
        return true;
    }

    private void startFetchProductService() {
        productNetworkDataSource.startFetchProductService();
    }

    private void deleteOldData() {
        productDAO.deleteAllProducts();
    }

    public LiveData<List<Product>> getProducts(String jenis) {
        initializeData();

        if (jenis.equals("semua")) {
            return productDAO.getAllProducts();
        } else {
            return productDAO.getAllProducts(jenis);
        }
    }

    public LiveData<List<Product>> getAllProducts() {
        initializeData();
        return productDAO.getAllProducts();
    }

    public void updateProduct(Product product) {
        new UpdateProduct().execute(product);
    }

    public void updateAllProduct(List<Product> productList) {

    }

    private class UpdateProduct extends AsyncTask<Product, Void, Void> {
        @Override
        protected Void doInBackground(Product... products) {
            productDAO.updateProduct(products[0]);
            return null;
        }
    }

    private class UpdateAllProduct extends AsyncTask<Product, Void, Void> {
        @Override
        protected Void doInBackground(Product... products) {
            return null;
        }
    }

}
