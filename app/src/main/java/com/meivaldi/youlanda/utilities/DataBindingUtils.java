package com.meivaldi.youlanda.utilities;

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

}
