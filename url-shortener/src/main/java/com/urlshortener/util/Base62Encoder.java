package com.urlshortener.util;

public class Base62Encoder {

    private static final String BASE62 =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int MIN_LENGHT = 6;

    public static String encode(long value) {
    	if(value==0) return  pad("a");
    	
        StringBuilder sb = new StringBuilder();

        while (value > 0) {
            int remainder = (int) (value % 62);
            sb.append(BASE62.charAt(remainder));
            value = value / 62;
        }

        return pad(sb.toString());
    }
    
    
    private static String pad(String shortKey) {
    	StringBuilder sb = new StringBuilder(shortKey);
    	
    	while(sb.length()<MIN_LENGHT) {
    		sb.insert(0, "a");
    	}
    	
    	return sb.toString();
    	
	}
}
