package com.meivaldi.youlanda.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.databinding.ActivityCheckoutBinding;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);

        toolbar = binding.toolbar;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rincian Pesanan");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

}
