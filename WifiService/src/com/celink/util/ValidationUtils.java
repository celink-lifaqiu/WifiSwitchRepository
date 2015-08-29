package com.celink.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean validateMoblie(String phone) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(phone);

        return m.matches();
    }

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


	public static boolean validateEmail(final String email) {
		Pattern p = Pattern.compile(EMAIL_PATTERN);
        Matcher m = p.matcher(email);

        return m.matches();

	}

    public static boolean validateNum(String num){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(num);
        if( !isNum.matches() )
        {
            return false;
        }
        return true;
    }

}
