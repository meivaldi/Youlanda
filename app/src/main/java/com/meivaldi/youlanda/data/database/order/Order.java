package com.meivaldi.youlanda.data.database.order;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.meivaldi.youlanda.BR;
import com.meivaldi.youlanda.data.database.cart.Cart;
import com.meivaldi.youlanda.data.database.discount.Discount;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order extends BaseObservable implements Serializable {

    private int id;
    private int cartSum;
    private int price;
    private int tax;
    private int total;
    private int diskon;
    private Discount mSpecial_discount;
    private int cash;
    private String jenis;
    private String time;
    private Date mDate;
    private List<Cart> cartList;

    public Order(int id, List<Cart> cartList, Date date) {
        this.id = id;
        this.cartSum = 0;
        this.price = 0;
        this.tax = 0;
        this.total = 0;
        this.diskon = 0;
        this.cartList = cartList;
        this.jenis = "Beli Langsung";
        this.cash = 0;
        mDate = date;
        mSpecial_discount = new Discount(0);
    }

    public Order(List<Cart> cartList, Date date) {
        this.id = 0;
        this.cartSum = 0;
        this.price = 0;
        this.tax = 0;
        this.total = 0;
        this.diskon = 0;
        this.cartList = cartList;
        this.jenis = "Beli Langsung";
        this.cash = 0;
        mDate = date;
        mSpecial_discount = new Discount(0);
    }

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
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
        price = total + tax - diskon - ((mSpecial_discount.getDiscount() * total) / 100);
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
    public int getDiskon() {
        return diskon;
    }

    public void setDiskon() {
        int temp = 0;

        for (Cart cart: cartList) {
            temp += ((Integer.valueOf(cart.getProduct().getHarga()) * cart.getQuantity()) * Float.valueOf(cart.getProduct().getDiskon()));
        }

        diskon = temp;
        notifyPropertyChanged(BR.diskon);
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

    @Bindable
    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
        notifyPropertyChanged(BR.jenis);
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    @Bindable
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
        notifyPropertyChanged(BR.date);
    }

    @Bindable
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    @Bindable
    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
        notifyPropertyChanged(BR.cash);
    }

    public Discount getSpecial_discount() {
        return mSpecial_discount;
    }

    public void setSpecial_discount(int discount) {
        Discount temp = new Discount(discount);
        mSpecial_discount = temp;
    }
}
