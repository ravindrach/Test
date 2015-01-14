// Copyright 2014 AdsWizz Inc. All Rights Reserved

package com.adswizz.ms.persomap.cache;

import static com.adswizz.ms.persomap.util.PersomapUtil.ASYNCH_GET_INTERVAL_SECONDS;
import static com.adswizz.ms.persomap.util.PersomapUtil.SEPARATOR;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Cache client implementation using Spymemcached
 * 
 * <pre>
 * @author AdsWizz Inc.
 * 
 */
public class CacheServiceImpl implements CacheService {

  private MemcachedClient memcachedClient = null;
  private int cacheExpiry = 0;
  private static Logger LOGGER = LoggerFactory.getLogger(CacheServiceImpl.class);

  public CacheServiceImpl(int cacheExpiry, MemcachedClient memcachedClient) {
    this.memcachedClient = memcachedClient;
    this.cacheExpiry = cacheExpiry * 60 * 60;
  }

  /**
   * Method to fetch PatrnerId on adswizzUserId and domain
   * 
   * @param adsWizzUserId
   * @param domain
   * @return PartnerUserId or null
   */
  public String getPartnerUserId(String adsWizzUserId, String domain) {
    String partnerUserId = null;
    try {
      partnerUserId =
          (String) memcachedClient.asyncGet(adsWizzUserId + SEPARATOR + domain).get(
              ASYNCH_GET_INTERVAL_SECONDS, TimeUnit.SECONDS);

    } catch (InterruptedException e) {
      LOGGER.error(" Error while getting partnerUserId from cache " + e.getMessage(), e);
    } catch (TimeoutException e) {
      LOGGER.error(" Error while getting partnerUserId from cache " + e.getMessage(), e);
    } catch (ExecutionException e) {
      LOGGER.error(" Error while getting partnerUserId from cache " + e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return partnerUserId;
  }

  /**
   * Method to get adswizzUserId on partnerUserId and domain
   * 
   * @param partnerUserId
   * @param domain
   * @return adswizzUserId or null
   */
  public String getAdsWizzUserId(String partnerUserId, String domain) {
    String adsWizzUserId = null;
    try {
      adsWizzUserId =
          (String) memcachedClient.asyncGet(partnerUserId + SEPARATOR + domain).get(
              ASYNCH_GET_INTERVAL_SECONDS, TimeUnit.SECONDS);

    } catch (InterruptedException e) {
      LOGGER.error(" Error while getting adsWizzUserId from cache " + e.getMessage(), e);
    } catch (TimeoutException e) {
      LOGGER.error(" Error while getting adsWizzUserId from cache " + e.getMessage(), e);
    } catch (ExecutionException e) {
      LOGGER.error(" Error while getting adsWizzUserId from cache " + e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return adsWizzUserId;
  }

  /**
   * Method to set the params into the memcached as appropriate key and value pairs
   * 
   * @param adsWizzUserId
   * @param partnerUserId
   * @param domain
   * @param lsDate
   * @param idKey
   */
  public void setUserMapping(String adsWizzUserId, String partnerUserId, String domain,
      String lsDate, String idKey) {

    try {
      String key = adsWizzUserId + SEPARATOR + domain;
      memcachedClient.set(key, cacheExpiry, partnerUserId + SEPARATOR + lsDate + SEPARATOR + idKey);

      key = partnerUserId + SEPARATOR + domain;
      memcachedClient.set(key, cacheExpiry, adsWizzUserId + SEPARATOR + lsDate + SEPARATOR + idKey);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /**
   * Method to delete record from memcached
   * 
   * @param adsWizzUserId
   * @param partnerUserId
   * @param domain
   */
  public void deleteUserMapping(String adsWizzUserId, String partnerUserId, String domain) {
    String key = adsWizzUserId + SEPARATOR + domain;
    memcachedClient.delete(key);

    key = partnerUserId + SEPARATOR + domain;
    memcachedClient.delete(key);
  }

}
