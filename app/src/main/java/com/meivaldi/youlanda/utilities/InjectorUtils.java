package com.meivaldi.youlanda.utilities;

import android.content.Context;

import com.meivaldi.youlanda.AppExecutors;
import com.meivaldi.youlanda.data.ProductRepository;
import com.meivaldi.youlanda.data.database.product.ProductDatabase;
import com.meivaldi.youlanda.data.network.ProductNetworkDataSource;
import com.meivaldi.youlanda.ui.MainViewModelFactory;

public class InjectorUtils {

    public static ProductRepository provideRepository(Context context) {
        ProductDatabase database = ProductDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        ProductNetworkDataSource networkDataSource =
                ProductNetworkDataSource.getInstance(context, executors);

        return ProductRepository.getInstance(database.productDAO(), networkDataSource, executors);
    }

    public static ProductNetworkDataSource provideNetworkDataSource(Context context) {
        provideRepository(context.getApplicationContext());
        AppExecutors appExecutors = AppExecutors.getInstance();
        return ProductNetworkDataSource.getInstance(context.getApplicationContext(), appExecutors);
    }

    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context, String jenis) {
        ProductRepository repository = provideRepository(context);
        return new MainViewModelFactory(repository, jenis);
    }
}
