// Copyright 2014 AdsWizz Inc. All Rights Reserved

package com.adswizz.ms.persomap.dao;

import java.util.List;

import com.adswizz.ms.persomap.model.PersoMapUser;

/**
 * This interface exposes methods for CRUD operations on User Mapping
 * 
 * @author AdsWizz Inc
 * 
 */
public interface PersoMapDao {

	// column names
	public static final String ADS_WIZZ_USER_ID_COLUMN = "adsWizzUserId";
	public static final String DOMAIN_COLUMN = "domain";
	public static final String ID_TYPE_COLUMN = "idType";
	public static final String PARTNER_USER_ID_COLUMN = "partnerUserId";

	/**
	 * Add PersoMap record in database
	 * 
	 * @param adsWizzUserId
	 * @param partnerUserID
	 * @param domain
	 * @param idType
	 */
	public PersoMapUser setUserMapping(String adsWizzUserId, String partnerUserId,
			String domain, String idType);

	/**
	 * Retrieve Partner Id
	 * 
	 * @param adsWizzUserId
	 * @param domain
	 * @param idType
	 * @return
	 */
	public String getPartnerUserId(String adsWizzUserId, String domain,
			String idType);

	/**
	 * Retrieve adsWizzId Id
	 * 
	 * @param partnerUserID
	 * @param domain
	 * @param idType
	 * @return
	 */
	public String getAdsWizzUserId(String partnerUserId, String domain,
			String idType);

	/**
	 * Update Last seen Date
	 * 
	 * @param adsWizzUserId
	 * @param partnerUserID
	 * @param domain
	 * @param idType
	 */
	public void updateLastSeenDate(String hashKey);

	/**
	 * Delete stale entries from database
	 * 
	 * @return List of deleted objects
	 */
	public List<PersoMapUser> deleteUserMappings();
}
