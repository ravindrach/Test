// Copyright 2014 AdsWizz Inc All Rights Reserved

package com.adswizz.ms.persomap.cache;

/**
 * This interface contains methods for cache interaction
 * 
 * @author Adswizz Inc.
 * 
 */
public interface CacheService {
  String getPartnerUserId(String adsWizzUserId, String domain);

  String getAdsWizzUserId(String partnerUserId, String domain);

  void setUserMapping(String adsWizzUserId, String partnerUserId, String domain, String lsDate,
      String idKey);

  void deleteUserMapping(String adsWizzUserId, String partnerUserId, String domain);

}
