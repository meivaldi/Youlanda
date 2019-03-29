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
    private int cash;
    private int special_discount;
    private String jenis;
    private String time;
    private String cashier;
    private String waiter;
    private Date mDate;
    private Discount discount;
    private List<Cart> cartList;

    public Order(int id, List<Cart> cartList, Date date) {
        this.id = id;
        this.cartSum = 0;
        this.price = 0;
        this.tax = 0;
        this.total = 0;
        this.diskon = 0;
        this.special_discount = 0;
        this.cartList = cartList;
        this.jenis = "";
        this.cash = 0;
        this.cashier = "Herjunot";
        this.waiter = "";
        this.discount = new Discount(0);
        mDate = date;
    }

    public Order(List<Cart> cartList, Date date) {
        this.id = 1;
        this.cartSum = 0;
        this.price = 0;
        this.tax = 0;
        this.total = 0;
        this.diskon = 0;
        this.special_discount = 0;
        this.cartList = cartList;
        this.jenis = "";
        this.cashier = "Herjunot";
        this.waiter = "";
        this.cash = 0;
        this.discount = new Discount(0);
        mDate = date;
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
        price = total + tax - diskon - special_discount;
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

    @Bindable
    public int getSpecial_discount() {
        return special_discount;
    }

    public void setSpecial_discount() {
        int temp = (total * discount.getDiscount()) / 100;

        special_discount = temp;
        notifyPropertyChanged(BR.special_discount);
    }

    @Bindable
    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
        notifyPropertyChanged(BR.cashier);
    }

    @Bindable
    public String getWaiter() {
        return waiter;
    }

    public void setWaiter(String waiter) {
        this.waiter = waiter;
        notifyPropertyChanged(BR.waiter);
    }

    @Bindable
    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
        notifyPropertyChanged(BR.discount);
    }
}
