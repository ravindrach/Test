// Copyright 2014 AdsWizz Inc. All Rights Reserved

package com.adswizz.ms.persomap.service;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;

import com.adswizz.ms.persomap.cache.CacheService;
import com.adswizz.ms.persomap.dao.PersoMapDao;
import com.adswizz.ms.persomap.model.PersoMapUser;

/**
 * Executor to delete stale entries from database
 * 
 * @author AdsWizz Inc.
 * 
 */
public final class DataCleanUpService implements Runnable {

	private PersoMapDao persoMapDao;
	private CacheService cacheService;
	private static final Logger LOGGER = org.slf4j.LoggerFactory
			.getLogger(DataCleanUpService.class);

	public DataCleanUpService(PersoMapDao persoMapDao, CacheService cacheService) {
		this.persoMapDao = persoMapDao;
		this.cacheService = cacheService;
	}

	@Override
	public void run() {
		LOGGER.info("Job started :", Calendar.getInstance().getTime());
		List<PersoMapUser> deletedObjects = persoMapDao.deleteUserMappings();
		for (PersoMapUser persoMapUser : deletedObjects) {
			cacheService.deleteUserMapping(persoMapUser.getAdsWizzUserId(),
					persoMapUser.getPartnerUserId(), persoMapUser.getDomain());
			LOGGER.info(
					"Entry with adsWizzUserId = {}, partnerUserId = {}, idType = {}, domain = {} deleted from DB and cache",
					persoMapUser.getAdsWizzUserId(),
					persoMapUser.getPartnerUserId(), persoMapUser.getIdType(),
					persoMapUser.getDomain());
		}
		LOGGER.info("Job completed ", Calendar.getInstance().getTime());
	}
}
