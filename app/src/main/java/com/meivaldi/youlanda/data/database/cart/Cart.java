package com.meivaldi.youlanda.data.database.cart;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.meivaldi.youlanda.BR;
import com.meivaldi.youlanda.data.database.product.Product;

@Entity(tableName = "cart")
public class Cart extends BaseObservable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private Product product;

    @Bindable
    private int quantity;

    public Cart(int id, @NonNull Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public Cart(@NonNull Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    @NonNull
    @Bindable
    public Product getProduct() {
        return product;
    }

    public void setProduct(@NonNull Product product) {
        this.product = product;
        notifyPropertyChanged(BR.product);
    }

    @Bindable
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }
}
