package com.meivaldi.youlanda.data.network;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.meivaldi.youlanda.utilities.InjectorUtils;

public class ProductFirebaseJobService extends JobService {

    private static final String TAG = ProductFirebaseJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(TAG, "Job service started");

        ProductNetworkDataSource networkDataSource = InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchProduct();

        jobFinished(job, false);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
