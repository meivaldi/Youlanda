package com.meivaldi.youlanda.utilities;

public class DataBindingUtils {

    public static String convert(String string) {
        return "Rp." + string + ",-";
    }

    public static String convertToString(int quantity) {
        return String.valueOf(quantity);
    }

}
