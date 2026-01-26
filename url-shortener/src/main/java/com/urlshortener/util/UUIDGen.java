package com.urlshortener.util;

import java.util.UUID;

public class UUIDGen {
	public static String generateShortKey() {
	    return UUID.randomUUID().toString().substring(0, 8);
	}
}

