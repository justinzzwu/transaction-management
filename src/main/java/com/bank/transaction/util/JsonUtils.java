package com.bank.transaction.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
	}

	public static String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			return null;
		}
	}
}
