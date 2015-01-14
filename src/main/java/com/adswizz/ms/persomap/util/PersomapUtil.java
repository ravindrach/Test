// Copyright 2014 AdsWizz Inc All Rights Reserved

package com.adswizz.ms.persomap.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Persomap Utility class.
 * 
 * <pre>
 * @author AdsWizz Inc.
 *
 */
public class PersomapUtil {
  private static Logger LOGGER = LoggerFactory.getLogger(PersomapUtil.class);
  public static final String SEPARATOR = "_";
  public static final int ASYNCH_GET_INTERVAL_SECONDS = 5;
  private static final String DATE_FORMAT = "MM/dd/yyyy";
  private static final String DEFAULT_TIME_ZONE = "GMT";

  /**
   * Method to parse Date
   * 
   * @param dateString
   * @return
   * @throws ParseException
   */
  public static Date parseDate(String dateString) throws ParseException {
    SimpleDateFormat dateFormat = getDateFormat();
    return dateFormat.parse(dateString);
  }

  /**
   * Method returns current system date in 'MM/dd/yyyy' format
   * 
   * @return
   */
  public static String getCurrentDate() {
    SimpleDateFormat dateFormat = getDateFormat();
    return dateFormat.format(new Date());
  }

  /**
   * Method returns new instance of SimpleDateFormat
   * 
   * @return
   */
  private static SimpleDateFormat getDateFormat() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    dateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
    return dateFormat;
  }


  /**
   * Update LastSeenDate in DB and cache if it > (current_date + maxLSD).
   * 
   * @param lastSeenDate
   * @param lstDays - No of days to update last seen date.
   * @return
   */
  public static boolean isLSDUpdateRequired(String lastSeenDate, int lstDays) {
    SimpleDateFormat dateFormat = getDateFormat();
    boolean isUpdate = false;
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(dateFormat.parse(lastSeenDate));
      cal.add(Calendar.DATE, lstDays);
      if (new Date().after(cal.getTime())) {
        isUpdate = true;
        LOGGER.debug("isUpdate flag set as true");
      }
    } catch (ParseException e) {
      LOGGER.error("Error in isLSDUpdate method while comparing date" + e.getMessage(), e);
    }
    return isUpdate;
  }

}
