package com.meivaldi.youlanda.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.meivaldi.youlanda.data.database.order.Order;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataBindingUtils {

    public static String convert(String string) {
        return "Rp." + string + ",-";
    }

    public static String convertToString(int quantity) {
        return String.valueOf(quantity);
    }

    public static String countTax(int price) {
        float tax = (price * 10) / 100;
        return "Rp." + String.valueOf(tax) + ",-";
    }

    public static String count(int cartSum) {
        return "Cart(" + cartSum + ")";
    }

    public static String currencyConvert(int price) {
        return "Rp." + price + ",-";
    }

    public static String stock(String stok) {
        return "Stok: " + stok;
    }

    public static String countDiscount(int diskon) {
        return "- Rp. " + diskon;
    }

    public static boolean isStockEmpty(String stok) {
        int stock = Integer.valueOf(stok);

        if (stock <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCartEmpty(int total) {
        if (total > 0) {
            return true;
        }

        return false;
    }

    public static String dateConverter(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE, dd/MMM/yyyy");

        return df.format(d);
    }

    public static String getOrderNumber(int number) {
        return "Order No #" + number;
    }

    public static String getReturn(Order order) {
        int price = order.getPrice();
        int cash = order.getCash();

        int change = cash - price;

        return "Rp." + change + ",-";
    }

    public static String stringConverter(int quantity) {
        return String.valueOf(quantity);
    }

    public static String discountConverter(String diskon) {
        int disc = (int) (Float.valueOf(diskon) * 100);

        return disc + "%";
    }

    public static boolean getDiscount(String diskon) {
        float disc = Float.valueOf(diskon);

        if (disc <= 0.0) {
            return false;
        } else {
            return true;
        }
    }

    public static String getCashier(String nama) {
        return "Kasir: " + nama;
    }

    public static String getWaiter(String nama) {
        return "Pelayan: " + nama;
    }

    public static String finalModal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SessionManager.PREF_NAME, 0);
        int finalModal = sharedPreferences.getInt(SessionManager.KEY_FINAL, 0);

        return "Rp. " + finalModal + ",-";
    }

    public static String starterModal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SessionManager.PREF_NAME, 0);
        int starterModal = sharedPreferences.getInt(SessionManager.KEY_STARTER, 0);

        return "Rp. " + starterModal + ",-";
    }

    public static String getProducts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SessionManager.PREF_NAME, 0);
        int products = sharedPreferences.getInt(SessionManager.KEY_PRODUCT, 0);

        return products + " Produk";
    }

    public static String getTransactions(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SessionManager.PREF_NAME, 0);
        int transactions = sharedPreferences.getInt(SessionManager.KEY_TRANSACTION, 0);

        return transactions + " Transaksi";
    }

}
