package com.tesi.unical.util;

import java.util.Collection;
import java.util.Map;

public class Utils {

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isCollectionEmpty(Collection<?>  o) {
        return o == null || o.isEmpty();
    }

    public static boolean isMapEmpty(Map<?,?> o) {
        return o == null || o.isEmpty();
    }
}
