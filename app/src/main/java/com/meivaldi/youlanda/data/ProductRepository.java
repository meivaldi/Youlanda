package com.meivaldi.youlanda.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.meivaldi.youlanda.AppExecutors;
import com.meivaldi.youlanda.data.database.karyawan.Karyawan;
import com.meivaldi.youlanda.data.database.karyawan.KaryawanDAO;
import com.meivaldi.youlanda.data.database.product.Product;
import com.meivaldi.youlanda.data.database.product.ProductDAO;
import com.meivaldi.youlanda.data.network.ProductNetworkDataSource;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ProductRepository {
    private static final String TAG = ProductRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static ProductRepository instance;

    private final ProductDAO productDAO;
    private final KaryawanDAO karyawanDAO;
    private final ProductNetworkDataSource productNetworkDataSource;
    private final AppExecutors appExecutors;

    private boolean initialized = false;

    public ProductRepository(ProductDAO productDAO,
                             KaryawanDAO karyawanDAO,
                             ProductNetworkDataSource productNetworkDataSource,
                             AppExecutors appExecutors) {
        this.productDAO = productDAO;
        this.karyawanDAO = karyawanDAO;
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
            ProductDAO productDAO, KaryawanDAO karyawanDAO, ProductNetworkDataSource productNetworkDataSource,
            AppExecutors appExecutors) {
        Log.d(TAG, "Getting repository");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new ProductRepository(productDAO, karyawanDAO, productNetworkDataSource, appExecutors);
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
        /*Date date = new Date();
        int hours = date.getHours();

        if ((hours % 4) == 0) {
            return true;
        } else {
            return false;
        }*/

        return true;
    }

    public Date getNormalizedUtcDateForToday() {
        long normalizedMilli = getNormalizedUtcMsForToday();
        return new Date(normalizedMilli);
    }

    public long getNormalizedUtcMsForToday() {
        long utcNowMillis = System.currentTimeMillis();
        TimeZone currentTimeZone = TimeZone.getTimeZone("GMT+07:00");
        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowMillis);
        long timeSinceEpochLocalTimeMillis = utcNowMillis + gmtOffsetMillis;
        long daysSinceEpochLocal = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocalTimeMillis);
        return TimeUnit.DAYS.toMillis(daysSinceEpochLocal);
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

    public void updateProduct(Product product) {
        new UpdateProduct().execute(product);
    }

    private class UpdateProduct extends AsyncTask<Product, Void, Void> {
        @Override
        protected Void doInBackground(Product... products) {
            productDAO.updateProduct(products[0]);
            return null;
        }
    }

    public void insertKaryawan(Karyawan karyawan) {
        new InsertKaryawan().execute(karyawan);
    }

    public class InsertKaryawan extends AsyncTask<Karyawan, Void, Void> {
        @Override
        protected Void doInBackground(Karyawan... karyawans) {
            karyawanDAO.insertKaryawan(karyawans[0]);
            return  null;
        }
    }

    public Karyawan getKaryawan() {
        Karyawan karyawan = null;
        try {
            karyawan = new GetKaryawan().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return karyawan;
    }

    public class GetKaryawan extends AsyncTask<Void, Void, Karyawan> {
        @Override
        protected Karyawan doInBackground(Void... voids) {
            Karyawan karyawan = karyawanDAO.getKaryawan();
            return karyawan;
        }
    }

    public void deleteKaryawan(Karyawan karyawan) {
        new DeleteKaryawan().execute(karyawan);
    }

    public class DeleteKaryawan extends AsyncTask<Karyawan, Void, Void> {
        @Override
        protected Void doInBackground(Karyawan... karyawans) {
            karyawanDAO.deleteKaryawan(karyawans[0]);
            return null;
        }
    }

}
