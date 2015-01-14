// Copyright 2014 AdsWizz Inc. All Rights Reserved
package com.adswizz.ms.persomap.resource.test;

import static com.adswizz.ms.persomap.util.PersomapUtil.SEPARATOR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import io.dropwizard.testing.junit.ResourceTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.adswizz.ms.persomap.cache.CacheService;
import com.adswizz.ms.persomap.config.PersoMapConfiguration;
import com.adswizz.ms.persomap.dao.PersoMapDao;
import com.adswizz.ms.persomap.resource.PersoMapResource;

/**
 * <p>
 * Test cases for {@link PersoMapResource}
 * </p>
 * 
 * @author AdsWizz Inc.
 *
 */
public class PersoMapResourceTest {

  private static final PersoMapDao persoMapDaoMock = mock(PersoMapDao.class);
  private static final PersoMapConfiguration configMock = mock(PersoMapConfiguration.class);
  private static final CacheService cacheMock = mock(CacheService.class);

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new PersoMapResource(persoMapDaoMock, cacheMock, configMock.getMaxRetainDays()))
      .build();

  @After
  public void tearDown() {
    reset(persoMapDaoMock);
    reset(configMock);
    reset(cacheMock);
  }

  @Before
  public void setup() {
    when(persoMapDaoMock.getAdsWizzUserId("part123", "adswizz.com", "COOKIE")).thenReturn(
        "uniqueID1" + SEPARATOR + "ads123" + SEPARATOR + "12/12/2014");
    when(persoMapDaoMock.getPartnerUserId("ads123", "adswizz.com", "COOKIE")).thenReturn(
        "uniqueID1" + SEPARATOR + "part123" + SEPARATOR + "12/12/2014");
    when(cacheMock.getPartnerUserId("ads456", "google.com")).thenReturn(
        "part456" + SEPARATOR + "12/12/2014" + SEPARATOR + "uniqueID2");
    when(cacheMock.getAdsWizzUserId("part456", "google.com")).thenReturn(
        "ads456" + SEPARATOR + "12/12/2014" + SEPARATOR + "uniqueID2");
  }

  /**
   * This test case return partner userId from cache.
   */
  @Test
  public void testGetPartnerIdFromCache() {
    String expectedResult =
        resources.client().resource("/v1/get_partner_uid/ads456/google.com/COOKIE")
            .get(String.class);
    Assert.assertEquals(expectedResult, "part456");
  }

  /**
   * This test case return partner userId from Database.
   */
  @Test
  public void testGetPartnerIdFromDB() {
    String expectedResult =
        resources.client().resource("/v1/get_partner_uid/ads123/adswizz.com/COOKIE")
            .get(String.class);
    Assert.assertEquals(expectedResult, "part123");
  }

  /**
   * This test case return adwizz userId from cache.
   */
  @Test
  public void testGetAdswizzIdFromCache() {
    String expectedResult =
        resources.client().resource("/v1/get_adswizz_uid/part456/google.com/COOKIE")
            .get(String.class);
    Assert.assertEquals(expectedResult, "ads456");
  }

  /**
   * This test case return adswizz userId from Database.
   */
  @Test
  public void testGetAdswizzIdFromDB() {
    when(cacheMock.getAdsWizzUserId("part123", "adswizz.com")).thenReturn(null);
    String expectedResult =
        resources.client().resource("/v1/get_adswizz_uid/part123/adswizz.com/COOKIE")
            .get(String.class);
    Assert.assertEquals(expectedResult, "ads123");
  }

}
