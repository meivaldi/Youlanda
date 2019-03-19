package com.meivaldi.youlanda.utilities;

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

}
