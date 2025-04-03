package com.bank.transaction.config;

import static org.junit.jupiter.api.Assertions.*;

import com.bank.transaction.util.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.interceptor.KeyGenerator;

import java.util.HashMap;
import java.util.Map;

class CacheConfigTest {

	private KeyGenerator keyGenerator;

	@BeforeEach
	void setUp() {
		CacheConfig cacheConfig = new CacheConfig();
		keyGenerator = cacheConfig.transactionListKeyGenerator();
	}

	@Test
	void testGenerateKeyWithMapParameter() {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("accountId", 12345);
		paramMap.put("type", "DEBIT");

		String key = keyGenerator.generate(null, null, paramMap).toString();

		String expectedJson = JsonUtils.toJson(paramMap);
		assertTrue(key.contains(expectedJson));
	}

	@Test
	void testGenerateKeyWithSortedMap() {
		Map<String, Object> map1 = new HashMap<>();
		map1.put("a", 1);
		map1.put("b", 2);

		Map<String, Object> map2 = new HashMap<>();
		map2.put("b", 2);
		map2.put("a", 1);

		String key1 = keyGenerator.generate(null, null, map1).toString();
		String key2 = keyGenerator.generate(null, null, map2).toString();

		assertEquals(key1, key2);
	}

	@Test
	void testGenerateKeyWithEmptyParameters() {
		String key = keyGenerator.generate(null, null).toString();
		assertEquals("", key);
	}
}