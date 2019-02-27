package com.meivaldi.youlanda.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.meivaldi.youlanda.utilities.InjectorUtils;

public class ProductSyncIntentService extends IntentService {
    private static final String TAG = ProductSyncIntentService.class.getSimpleName();

    public ProductSyncIntentService() {
        super(ProductSyncIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent service created");
        ProductNetworkDataSource networkDataSource = InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchProduct();
    }
}
