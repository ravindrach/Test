// Copyright 2014 AdsWizz Inc. All Rights Reserved

package com.adswizz.ms.persomap.resource;

import static com.adswizz.ms.persomap.util.PersomapUtil.SEPARATOR;
import static com.adswizz.ms.persomap.util.PersomapUtil.isLSDUpdateRequired;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adswizz.ms.persomap.cache.CacheService;
import com.adswizz.ms.persomap.dao.PersoMapDao;
import com.adswizz.ms.persomap.model.PersoMapUser;
import com.adswizz.ms.persomap.util.PersomapUtil;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * <pre>
 *  This resource class have all rest api associated with a resepctive URI patteren.
 * On matching for request URI pattern the correspounding api will be served.
 * 
 * <pre>
 * @author AdsWizz Inc
 *
 */
@Path("/v1")
@Api(value = "/PersoMapAPI")
public class PersoMapResource {

  private final PersoMapDao persoMapDao;
  private final CacheService cacheService;
  private final int maxLastUpdateDays;
  private static Logger LOGGER = LoggerFactory.getLogger(PersoMapResource.class);

  public PersoMapResource(PersoMapDao persoMapDao, CacheService cacheService, int maxLastUpdateDays) {
    this.persoMapDao = persoMapDao;
    this.cacheService = cacheService;
    this.maxLastUpdateDays = maxLastUpdateDays;
  }

  /**
   * This method return the partnerUserId for given partnerUserId, domain and Idtype.
   * 
   * @param adsWizzUserId
   * @param domain
   * @param idType
   * @return partnerUserId
   */
  @GET
  @Path("/get_partner_uid/{adsWizzUserId}/{domain}/{idType}")
  @ApiOperation(value = "Get the partnerUserId for given adswizzId, domain and idType")
  @ApiResponses({
      @ApiResponse(code = HttpServletResponse.SC_OK,
          message = "Successfully returned the partner User Id."),
      @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT,
          message = "No record found for given parameters."),
      @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST,
          message = "Invalid input. Please provide valid parameters."),
      @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          message = "Internal server error while getting the data from cache or database.")})
  public Response getPartnerUserId(
      @ApiParam(name = "adsWizzUserId", value = "AdsWizz User Id") @PathParam("adsWizzUserId") String adsWizzUserId,
      @ApiParam(name = "domain", value = "Domain Name") @PathParam("domain") String domain,
      @ApiParam(name = "idType", value = "IDType") @PathParam("idType") String idType) {

    LOGGER.info("Inside GetPartnerUserId method parameters are adsWizzUserId= " + adsWizzUserId
        + " domain= " + domain + "idType= " + idType);

    if (adsWizzUserId == null || domain == null || idType == null)
      return Response.status(Response.Status.BAD_REQUEST).build();
    String partnerUserId = null;


    // Get from Cache first
    LOGGER.info("Checking the record in Cache");
    String value = cacheService.getPartnerUserId(adsWizzUserId, domain);
    if (value != null) {
      LOGGER.info("Found the record in the cache. Processing the record");
      // Value exists in the cache
      String[] splitString = value.split(SEPARATOR);
      if (splitString.length > 2) {
        partnerUserId = splitString[0];
        String lastSeenDate = splitString[1];
        String idKey = splitString[2];
        try {
          if (isLSDUpdateRequired(lastSeenDate, maxLastUpdateDays)) {
            // Code to update lastSeenDate in db
            LOGGER.info("Updating the last seen date in DB");
            persoMapDao.updateLastSeenDate(idKey);
            LOGGER.info("Updating the last seen date in Cache");
            cacheService.setUserMapping(adsWizzUserId, partnerUserId, domain,
                PersomapUtil.getCurrentDate(), idKey);
          }
        } catch (Exception e) {
          LOGGER.error("Error while Updating the last seen date" + e.getMessage(), e);
        }
      }
      return Response.ok(partnerUserId, MediaType.TEXT_PLAIN).build();
    }

    // Get from DB
    String partnerWithIdKey = null;
    try {
      LOGGER.info("Partner UserId checking in DB.");
      partnerWithIdKey = persoMapDao.getPartnerUserId(adsWizzUserId, domain, idType);
    } catch (Exception ex) {
      LOGGER.error("Error while retrieving PartnerUserId  " + ex.getMessage(), ex);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    if (partnerWithIdKey == null) {
      return Response.status(Response.Status.NO_CONTENT).build();
    }

    String[] splitString = partnerWithIdKey.split(SEPARATOR);
    if (splitString.length > 2) {
      String idKey = splitString[0];
      partnerUserId = splitString[1];
      String lsdFromDB = splitString[2];

      // set lsd in cache.
      if (isLSDUpdateRequired(lsdFromDB, maxLastUpdateDays)) {
        persoMapDao.updateLastSeenDate(idKey);
        cacheService.setUserMapping(adsWizzUserId, partnerUserId, domain,
            PersomapUtil.getCurrentDate(), idKey);
      } else {
        cacheService.setUserMapping(adsWizzUserId, partnerUserId, domain, lsdFromDB, idKey);
      }
    }

    return Response.ok(partnerUserId, MediaType.TEXT_PLAIN).build();
  }

  /**
   * This method return the AdswizzuserId for given partnerUserId, domain and Idtype.
   * 
   * @param partnerUserId
   * @param domain
   * @param idType
   * @return adswizzuserId
   */
  @GET
  @Path("/get_adswizz_uid/{partnerUserId}/{domain}/{idType}")
  @ApiOperation("Get the adswizzUserId for given partnerUserId, domain and Idtype")
  @ApiResponses({
      @ApiResponse(code = HttpServletResponse.SC_OK,
          message = "Successfully returned the Adswizz User Id. "),
      @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT,
          message = "No record found for given parameters."),
      @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST,
          message = "Invalid input. Please provide valid parameters."),
      @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          message = "Internal server error while getting the data from cache or database.")})
  public Response getAdsWizzUserId(
      @ApiParam(name = "partnerUserId", value = "Partner User Id") @PathParam("partnerUserId") String partnerUserId,
      @ApiParam(name = "domain", value = "Domain Name") @PathParam("domain") String domain,
      @ApiParam(name = "idType", value = "IDType") @PathParam("idType") String idType) {

    LOGGER.info("Inside getAdsWizzUserId method parameters are partnerUserId= " + partnerUserId
        + " domain= " + domain + "idType= " + idType);

    if (partnerUserId == null || domain == null || idType == null)
      return Response.status(Response.Status.BAD_REQUEST).build();
    String adsWizzUserId = null;

    // Get from Cache first
    LOGGER.info("Checking the record in Cache");
    String value = cacheService.getAdsWizzUserId(partnerUserId, domain);
    if (value != null) {
      LOGGER.info("Found the record in the cache. Processing the record");
      // Value exists in the cache
      String[] splitString = value.split(SEPARATOR);
      if (splitString.length > 2) {
        adsWizzUserId = splitString[0];
        String lastSeenDate = splitString[1];
        String idKey = splitString[2];
        try {
          if (isLSDUpdateRequired(lastSeenDate, maxLastUpdateDays)) {
            // Code to update lastSeenDate in db
            LOGGER.info("Updating the last seen date in DB");
            persoMapDao.updateLastSeenDate(idKey);
            LOGGER.info("Updating the last seen date in Cache");
            cacheService.setUserMapping(adsWizzUserId, partnerUserId, domain,
                PersomapUtil.getCurrentDate(), idKey);
          }
        } catch (Exception e) {
          LOGGER.error("Error while Updating the last seen date" + e.getMessage(), e);
        }
      }
      return Response.ok(adsWizzUserId, MediaType.TEXT_PLAIN).build();
    }

    String adsWizzIdWithIdKey = null;
    // Get from DB
    try {
      LOGGER.info("Adswizz UserId checking in DB.");
      adsWizzIdWithIdKey = persoMapDao.getAdsWizzUserId(partnerUserId, domain, idType);
    } catch (Exception ex) {
      LOGGER.error("Error while retrieving AdswizzUserId  " + ex.getMessage(), ex);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    if (adsWizzIdWithIdKey == null)
      return Response.status(Response.Status.NO_CONTENT).build();

    String[] splitString = adsWizzIdWithIdKey.split(SEPARATOR);
    if (splitString.length > 2) {
      String idKey = splitString[0];
      adsWizzUserId = splitString[1];
      String lsdFromDB = splitString[2];

      // set lsd in cache.
      if (isLSDUpdateRequired(lsdFromDB, maxLastUpdateDays)) {
        persoMapDao.updateLastSeenDate(idKey);
        cacheService.setUserMapping(adsWizzUserId, partnerUserId, domain,
            PersomapUtil.getCurrentDate(), idKey);
      } else {
        cacheService.setUserMapping(adsWizzUserId, partnerUserId, domain, lsdFromDB, idKey);
      }
    }
    return Response.ok(adsWizzUserId, MediaType.TEXT_PLAIN).build();
  }

  /**
   * This method save the user mapping in database.
   * 
   * @param adsWizzUserId
   * @param partnerUserId
   * @param domain
   * @param idType
   */
  @POST
  @Path("/set_user_mapping/{adsWizzUserId}/{partnerUserId}/{domain}/{idType}")
  @ApiOperation("Store the user mapping.")
  @ApiResponses({
      @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Record sucessfully created.  "),
      @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST,
          message = "Invalid input. Please provide valid parameters."),
      @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          message = "Internal server error while setting the data from cache or database.")})
  public Response setUserMapping(
      @ApiParam(name = "adsWizzUserId", value = "AdsWizz User Id") @PathParam("adsWizzUserId") String adsWizzUserId,
      @ApiParam(name = "partnerUserId", value = "Partner User Id") @PathParam("partnerUserId") String partnerUserId,
      @ApiParam(name = "domain", value = "Domain Name") @PathParam("domain") String domain,
      @ApiParam(name = "idType", value = "IDType") @PathParam("idType") String idType) {

    LOGGER.info("SetUserMapping method parameters are adsWizzUserId= " + adsWizzUserId
        + " partnerUserID= " + partnerUserId + " domain= " + domain + "idType" + idType);

    if (partnerUserId == null || domain == null || idType == null)
      return Response.status(Response.Status.BAD_REQUEST).build();
    try {
      PersoMapUser persomapUser =
          persoMapDao.setUserMapping(adsWizzUserId, partnerUserId, domain, idType);
      // Store records in cache.
      cacheService.setUserMapping(adsWizzUserId, partnerUserId, domain,
          PersomapUtil.getCurrentDate(), persomapUser.getId());
    } catch (Exception ex) {
      LOGGER.error("Error while adding user mapping record " + ex.getMessage(), ex);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }

    return Response.status(Response.Status.CREATED).build();
  }

}
