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

}
