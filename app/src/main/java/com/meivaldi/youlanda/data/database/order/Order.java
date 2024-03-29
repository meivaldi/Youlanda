package com.meivaldi.youlanda.data.database.order;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.meivaldi.youlanda.BR;
import com.meivaldi.youlanda.data.database.cart.Cart;

import java.util.List;

public class Order extends BaseObservable {

    private int cartSum;
    private int price;
    private int tax;
    private int total;
    private List<Cart> cartList;

    public Order(List<Cart> cartList) {
        this.cartSum = 0;
        this.price = 0;
        this.tax = 0;
        this.total = 0;
        this.cartList = cartList;
    }

    @Bindable
    public int getCartSum() {
        return cartSum;
    }

    public void setCartSum(int cartSum) {
        this.cartSum = cartSum;
        notifyPropertyChanged(BR.cartSum);
    }

    @Bindable
    public int getPrice() {
        return price;
    }

    public void setPrice() {
        price = total + tax;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public int getTax() {
        return tax;
    }

    public void setTax() {
        tax = (total * 10) / 100;
        notifyPropertyChanged(BR.tax);
    }

    @Bindable
    public int getTotal() {
        return total;
    }

    public void setTotal() {
        int temp = 0;

        for (Cart cart: cartList) {
            temp += (Integer.valueOf(cart.getProduct().getHarga()) * cart.getQuantity());
        }

        total = temp;
        notifyPropertyChanged(BR.total);
    }

}
