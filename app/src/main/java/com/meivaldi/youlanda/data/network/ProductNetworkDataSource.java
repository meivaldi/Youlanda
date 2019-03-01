package com.meivaldi.youlanda.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.meivaldi.youlanda.AppExecutors;
import com.meivaldi.youlanda.data.database.Product;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductNetworkDataSource {

    private static final String LOG_TAG = ProductNetworkDataSource.class.getSimpleName();
    private static final int SYNC_INTERVAL_HOURS = 1;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String PRODUCT_SYNC_TAG = "product-sync";

    private static final Object LOCK = new Object();
    private static ProductNetworkDataSource instance;
    private final Context context;

    private final MutableLiveData<List<Product>> mDownloadedProducts;
    private final AppExecutors executors;

    public ProductNetworkDataSource(Context context, AppExecutors executors) {
        this.context = context;
        this.executors = executors;
        mDownloadedProducts = new MutableLiveData<>();
    }

    public static ProductNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting network data source");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new ProductNetworkDataSource(context, executors);
                Log.d(LOG_TAG, "Network data source instance created");
            }
        }

        return instance;
    }

    public void startFetchProductService() {
        Intent intent = new Intent(context, ProductSyncIntentService.class);
        context.startService(intent);
        Log.d(LOG_TAG, "Service created");
    }

    public LiveData<List<Product>> getCurrentProducts() {
        return mDownloadedProducts;
    }

    public void scheduleRecurringFetchProductSync() {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);

        Job productJob = jobDispatcher.newJobBuilder()
                .setService(ProductFirebaseJobService.class)
                .setTag(PRODUCT_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_FLEXTIME_SECONDS + SYNC_INTERVAL_SECONDS))
                .setReplaceCurrent(true)
                .build();

        jobDispatcher.schedule(productJob);
        Log.d(LOG_TAG, "Job scheduled");
    }

    public void fetchProduct() {
        Log.d(LOG_TAG, "Fetch product started");
        executors.networkIO().execute(() -> {
            try {
                GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                Call<List<Product>> call = service.getAllBreads();
                call.enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        mDownloadedProducts.postValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(context, "Gagal", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
