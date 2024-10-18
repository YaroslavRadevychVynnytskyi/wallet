package com.nerdysoft.annotation.util;

public class StringUtil {
    public static String extractDomainModelName(String controllerName) {
        return controllerName.substring(0, controllerName.length() - "Controller". length());
    }

    public static String capitalizeFirstChar(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
