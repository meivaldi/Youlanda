package com.meivaldi.youlanda.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.utilities.InjectorUtils;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        recyclerView = findViewById(R.id.recycler_view);

        viewModel.getProductList().observe(this, products -> {

        });
    }
}
