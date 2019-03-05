package com.meivaldi.youlanda.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.meivaldi.youlanda.data.ProductRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final ProductRepository repository;
    private String jenis;

    public MainViewModelFactory(ProductRepository repository, String jenis) {
        this.repository = repository;
        this.jenis = jenis;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(repository, jenis);
    }
}
