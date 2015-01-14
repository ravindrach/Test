// Copyright 2014 AdsWizz Inc All Rights Reserved

package com.adswizz.ms.persomap.config;

import io.dropwizard.Configuration;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <pre>
 * This class represent getter and setter for all configuration
 * properties present in persomap-config.yml.
 * 
 * </pre>
 * 
 * @author AdsWizz Inc
 *
 */

public class PersoMapConfiguration extends Configuration {

  @NotNull
  private int maxRetainDays;
  @NotNull
  private int maxLastUpdateDays;
  @NotEmpty
  private String accessKeyId;
  @NotEmpty
  private String secretAccessKey;
  @NotEmpty
  private String serviceName;
  @NotEmpty
  private String region;
  @NotNull
  private int maxConnections;
  @NotNull
  private int connectionTimeout;
  @NotNull
  private int maxErrorRetry;
  @NotEmpty
  private String cacheURL;
  @NotNull
  private int cacheExpiry;
  @NotEmpty
  private String dynamoDBTable;
  @NotEmpty
  private String swaggerHost;
  @NotNull
  private int jobDelay;
  @NotNull
  private int jobPeriod;

  @JsonProperty(value = "job_run")
  private boolean jobRun;

  @JsonProperty(value = "spring_beans_path")
  private String beansPath;

  public String getBeansPath() {
    return beansPath;
  }

  public void setBeansPath(String beansPath) {
    this.beansPath = beansPath;
  }

  @JsonProperty("max_last_update_days")
  public int getMaxLastUpdateDays() {
    return maxLastUpdateDays;
  }

  @JsonProperty("max_last_update_days")
  public void setMaxLastUpdateDays(int maxLastUpdateDays) {
    this.maxLastUpdateDays = maxLastUpdateDays;
  }

  @JsonProperty("access_key_id")
  public String getAccessKeyId() {
    return accessKeyId;
  }

  @JsonProperty("access_key_id")
  public void setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
  }

  @JsonProperty("secret_access_key")
  public String getSecretAccessKey() {
    return secretAccessKey;
  }

  @JsonProperty("secret_access_key")
  public void setSecretAccessKey(String secretAccessKey) {
    this.secretAccessKey = secretAccessKey;
  }

  @JsonProperty("aws_endpoint_url")
  public String getServiceName() {
    return serviceName;
  }

  @JsonProperty("aws_endpoint_url")
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @JsonProperty("aws_region_name")
  public String getRegion() {
    return region;
  }

  @JsonProperty("aws_region_name")
  public void setRegion(String region) {
    this.region = region;
  }

  @JsonProperty("max_connections")
  public int getMaxConnections() {
    return maxConnections;
  }

  @JsonProperty("max_connections")
  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  @JsonProperty("connection_timeout")
  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  @JsonProperty("connection_timeout")
  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  @JsonProperty("max_error_retry")
  public int getMaxErrorRetry() {
    return maxErrorRetry;
  }

  @JsonProperty("max_error_retry")
  public void setMaxErrorRetry(int maxErrorRetry) {
    this.maxErrorRetry = maxErrorRetry;
  }

  @JsonProperty("aws_cache_url")
  public String getCacheURL() {
    return cacheURL;
  }

  @JsonProperty("aws_cache_url")
  public void setCacheURL(String cacheURL) {
    this.cacheURL = cacheURL;
  }

  @JsonProperty("dynamodb_table_name")
  public String getDynamoDBTable() {
    return dynamoDBTable;
  }

  @JsonProperty("dynamodb_table_name")
  public void setDynamoDBTable(String dynamoDBTable) {
    this.dynamoDBTable = dynamoDBTable;
  }

  @JsonProperty("max_retain_days")
  public int getMaxRetainDays() {
    return maxRetainDays;
  }

  @JsonProperty("max_retain_days")
  public void setMaxRetainDays(int maxRetainDays) {
    this.maxRetainDays = maxRetainDays;
  }

  @JsonProperty("cache_expiry")
  public int getCacheExpiry() {
    return cacheExpiry;
  }

  @JsonProperty("cache_expiry")
  public void setCacheExpiry(int cacheExpiry) {
    this.cacheExpiry = cacheExpiry;
  }

  @JsonProperty("swagger_host_name")
  public String getSwaggerHost() {
    return swaggerHost;
  }

  @JsonProperty("swagger_host_name")
  public void setSwaggerHost(String swaggerHost) {
    this.swaggerHost = swaggerHost;
  }

  @JsonProperty("job_delay")
  public int getJobDelay() {
    return jobDelay;
  }

  @JsonProperty("job_delay")
  public void setJobDelay(int jobDelay) {
    this.jobDelay = jobDelay;
  }

  @JsonProperty("job_period")
  public int getJobPeriod() {
    return jobPeriod;
  }

  @JsonProperty("job_period")
  public void setJobPeriod(int jobPeriod) {
    this.jobPeriod = jobPeriod;
  }
  
  public boolean isJobRun() {
    return jobRun;
  }

  public void setJobRun(boolean jobRun) {
    this.jobRun = jobRun;
  }


}
