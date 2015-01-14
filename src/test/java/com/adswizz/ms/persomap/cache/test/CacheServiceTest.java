package com.adswizz.ms.persomap.cache.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.adswizz.ms.persomap.cache.CacheService;
import com.adswizz.ms.persomap.cache.CacheServiceImpl;
import com.adswizz.ms.persomap.util.PersomapUtil;
import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;

/**
 * <p>
 * Test cases for CacheService
 * </p>
 * 
 * @author Adswizz Inc.
 * 
 */
public class CacheServiceTest {

	private CacheService cacheService = null;
	private MemcachedClient memcachedClient = null;
	private MemCacheDaemon<LocalCacheElement> daemon = null;
	private String date = PersomapUtil.getCurrentDate();

	/**
	 * Initialization to be called in every test method
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Before
	public void initMemcachedClient() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {

		// Initialize the daemon server
		daemon = new MemCacheDaemon<LocalCacheElement>();
		int portForUse = 9998;
		CacheStorage<com.thimbleware.jmemcached.Key, LocalCacheElement> storage;
		InetSocketAddress c = new InetSocketAddress(portForUse);
		storage = ConcurrentLinkedHashMap.create(
				ConcurrentLinkedHashMap.EvictionPolicy.LRU, 1000000, 469762048);
		daemon.setCache(new CacheImpl(storage));
		daemon.setBinary(true);
		daemon.setAddr(c);
		daemon.start();

		// Initialize the memcachedClient
		try {
			InetSocketAddress c2 = new InetSocketAddress(portForUse);
			List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
			list.add(c2);
			memcachedClient = new MemcachedClient(
					new BinaryConnectionFactory(), list);

			cacheService = new CacheServiceImpl(24, memcachedClient);
			// Set the test data
			cacheService.setUserMapping("1", "1", "1", date, "1");
			cacheService.setUserMapping("2", "2", "2", date, "2");
			cacheService.setUserMapping("3", "3", "3", date, "3");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void shutdown() {
		memcachedClient.shutdown();
		daemon.stop();
	}

	/**
	 * Test case to fetch Partner Id on adswizzUserId, domain and id
	 * 
	 */
	@Test
	public void testGetPartnerUserId() {
		assertEquals("1_" + date + "_1",
				cacheService.getPartnerUserId("1", "1"));
	}

	/**
	 * Test case to fetch AdsWizzUserId on partnerUserId, domain and id
	 * 
	 */
	@Test
	public void testGetAdsWizzUserId() {
		assertEquals("2_" + date + "_2",
				cacheService.getAdsWizzUserId("2", "2"));
	}

	/**
	 * Method to test set User mapping
	 */
	@Test
	public void testSetUserMapping() {
		cacheService.setUserMapping("4", "4", "4", date, "4");
		assertEquals("4_" + date + "_4",
				cacheService.getAdsWizzUserId("4", "4"));
	}

	/**
	 * Method to test delete User mapping
	 */
	@Test
	public void testDeleteUserMapping() {
		cacheService.deleteUserMapping("4", "4", "4");
		assertEquals("", cacheService.getAdsWizzUserId("4", "4"));
	}

}
