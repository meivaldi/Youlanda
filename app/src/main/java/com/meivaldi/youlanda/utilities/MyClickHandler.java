package com.meivaldi.youlanda.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.ui.CheckoutActivity;

public class MyClickHandler {

    private static Context context;

    public MyClickHandler(Context context) {
        this.context = context;
    }

    public static void purchase(View view) {
        Intent intent = new Intent(context, CheckoutActivity.class);
        context.startActivity(intent);
    }

}

