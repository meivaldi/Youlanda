package com.meivaldi.youlanda.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class MyClickHandler {

    private static Context context;

    public MyClickHandler(Context context) {
        this.context = context;
    }

    public static void purchase(View view) {
        //context.startActivity(new Intent(context, CheckoutActivity.class));
    }

}

