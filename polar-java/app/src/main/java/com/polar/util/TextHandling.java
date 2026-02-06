package com.polar.util;

import java.util.Locale;

public class TextHandling {

    public static int parse(String a) {
        int w = 0, i = 0, length = a.length(), k;

        if (length == 0)
            return 0;

        do {
            k = a.charAt(i++);
            if (k < 48 || k > 59)
                return 0;
            w = 10 * w + k - 48;
        } while (i < length);

        return w;
    }

    public static String getString(double k) {
        return String.format(Locale.ENGLISH, "%f", k);
    }
}
