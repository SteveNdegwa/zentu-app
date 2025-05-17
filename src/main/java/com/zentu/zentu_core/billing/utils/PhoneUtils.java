package com.zentu.zentu_core.billing.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtils {

    public static String normalizePhoneNumber(String phone, String countryCode, Integer totalCount) {
        try {
            if (countryCode == null) {
                countryCode = "254";
            }
            if (totalCount == null) {
                totalCount = 12;
            }
            phone = phone.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
            if (phone.startsWith("+")) {
                phone = phone.substring(1);
            }
            if (phone.length() == totalCount) {
                return phone;
            } else if ((phone.length() + countryCode.length()) == totalCount) {
                return countryCode + phone;
            } else if (phone.startsWith("0") && ((phone.length() - 1) + countryCode.length()) == totalCount) {
                return countryCode + phone.substring(1);
            } else {
                if (countryCode.length() > 0) {
                    int overlap = Math.abs((phone.length() + countryCode.length()) - totalCount);
                    return countryCode + phone.substring(overlap - 1);
                } else {
                    return phone;
                }
            }
        } catch (Exception ex) {
            System.out.println("normalizePhoneNumber Exception: " + ex.getMessage());
        }
        return "";
    }

    public static boolean validatePhoneNumber(String phoneNumber, String countryCode, Integer totalCount) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber, countryCode, totalCount);
        Pattern pattern = Pattern.compile("^[0-9]{9,15}$");
        Matcher matcher = pattern.matcher(normalizedPhone);
        return matcher.matches();
    }
}
