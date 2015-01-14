// Copyright 2014 AdsWizz Inc. All Rights Reserved

package com.adswizz.ms.persomap.dao;

import static com.adswizz.ms.persomap.util.PersomapUtil.SEPARATOR;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.adswizz.ms.persomap.model.PersoMapUser;
import com.adswizz.ms.persomap.util.PersomapUtil;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

/**
 * This class performs CRUD operations on User Mapping
 * 
 * @author AdsWizz Inc
 * 
 */
public class PersoMapDaoImpl implements PersoMapDao {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PersoMapDaoImpl.class);
  private final DynamoDBMapper mapper;
  private final int maxRetainDays;

  public PersoMapDaoImpl(DynamoDBMapper dynamoDbMapper, int maxRetainDays) {
    this.mapper = dynamoDbMapper;
    this.maxRetainDays = maxRetainDays;
  }

  /**
   * Add PersoMap record in database
   * 
   * @param adsWizzUserId
   * @param partnerUserID
   * @param domain
   * @param idType
   */
  public PersoMapUser setUserMapping(String adsWizzUserId, String partnerUserId, String domain,
      String idType) {
    String dateString = PersomapUtil.getCurrentDate();
    PersoMapUser user = new PersoMapUser();
    user.setAdsWizzUserId(adsWizzUserId);
    user.setDomain(domain);
    user.setIdType(idType);
    user.setPartnerUserId(partnerUserId);
    user.setCreationDate(dateString);
    user.setLastSeenDate(dateString);
    mapper.save(user);
    LOGGER.info("USer Mapping created for AdsWizz user id : {" + adsWizzUserId + "}, partner Id {"
        + partnerUserId + "} , domain {" + domain + "} and idType {" + idType + "} ");
    return user;
  }

  /**
   * Retrieve Partner Id
   * 
   * @param adsWizzUserId
   * @param domain
   * @param idType
   * @return
   */
  public String getPartnerUserId(String adsWizzUserId, String domain, String idType) {
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    Map<String, Condition> scanFilter = new HashMap<String, Condition>();
    scanFilter.put(
        ADS_WIZZ_USER_ID_COLUMN,
        new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
            new AttributeValue(adsWizzUserId)));
    scanFilter.put(DOMAIN_COLUMN, new Condition().withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(new AttributeValue(domain)));
    scanFilter.put(ID_TYPE_COLUMN, new Condition().withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(new AttributeValue(idType)));
    scanExpression.withScanFilter(scanFilter);
    PaginatedScanList<PersoMapUser> scanList = mapper.scan(PersoMapUser.class, scanExpression);
    if (scanList != null && scanList.size() > 0) {
      PersoMapUser persoMapUser = scanList.get(0);
      return persoMapUser.getId() + SEPARATOR + persoMapUser.getPartnerUserId() + SEPARATOR
          + persoMapUser.getLastSeenDate();
    } else {
      return null;
    }
  }

  /**
   * Retrieve adsWizzId Id
   * 
   * @param partnerUserID
   * @param domain
   * @param idType
   * @return
   */
  public String getAdsWizzUserId(String partnerUserId, String domain, String idType) {
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    Map<String, Condition> scanFilter = new HashMap<String, Condition>();
    scanFilter.put(
        PARTNER_USER_ID_COLUMN,
        new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
            new AttributeValue(partnerUserId)));
    scanFilter.put(DOMAIN_COLUMN, new Condition().withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(new AttributeValue(domain)));
    scanFilter.put(ID_TYPE_COLUMN, new Condition().withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(new AttributeValue(idType)));
    scanExpression.withScanFilter(scanFilter);
    PaginatedScanList<PersoMapUser> scanList = mapper.scan(PersoMapUser.class, scanExpression);
    if (scanList != null && scanList.size() > 0) {
      PersoMapUser persoMapUser = scanList.get(0);
      return persoMapUser.getId() + SEPARATOR + persoMapUser.getAdsWizzUserId() + SEPARATOR
          + persoMapUser.getLastSeenDate();
    } else {
      return null;
    }
  }

  /**
   * Update Last seen Date
   * 
   * @param adsWizzUserId
   * @param partnerUserID
   * @param domain
   * @param idType
   */
  public void updateLastSeenDate(String idKey) {
    PersoMapUser persoMapUser = mapper.load(PersoMapUser.class, idKey);
    persoMapUser.setLastSeenDate(PersomapUtil.getCurrentDate());
    mapper.save(persoMapUser);
    LOGGER.info("Last seen Date is updated for AdsWizz user id : {}, partner Id {}",
        persoMapUser.getAdsWizzUserId(), persoMapUser.getPartnerUserId());
  }

  /**
   * Delete stale entries from database
   * 
   * @return List of deleted objects
   */
  public List<PersoMapUser> deleteUserMappings() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -maxRetainDays);
    Date retainDate = cal.getTime();
    List<PersoMapUser> objectsToDelete = new ArrayList<PersoMapUser>();
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    List<PersoMapUser> scanResult = mapper.scan(PersoMapUser.class, scanExpression);
    for (PersoMapUser persoMapUser : scanResult) {
      Date dbDate;
      try {
        dbDate = PersomapUtil.parseDate(persoMapUser.getLastSeenDate());
        if (retainDate.compareTo(dbDate) == 1) {
          objectsToDelete.add(persoMapUser);
        }
      } catch (ParseException e) {
        LOGGER.error("Error in Parsing Last seen date in background job " + e.getMessage(), e);
      }
    }
    mapper.batchDelete(objectsToDelete);
    LOGGER.info("Job deleted {} entries which are older than {}", objectsToDelete.size(),
        retainDate);
    return objectsToDelete;
  }
}
