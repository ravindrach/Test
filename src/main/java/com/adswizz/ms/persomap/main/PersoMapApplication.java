// Copyright 2014 AdsWizz Inc, All Rights Reserved.

package com.adswizz.ms.persomap.main;

import io.dropwizard.Application;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerDropwizard;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.adswizz.ms.persomap.config.PersoMapConfiguration;

/**
 * <p>
 * This class is entry point of persomap application wherein all application related configuration
 * are bundle and configured and, registering the resource.
 * 
 * </p>
 * 
 * @author AdsWizz Inc.
 * 
 */
public class PersoMapApplication extends Application<PersoMapConfiguration> {

  private static Logger LOGGER = LoggerFactory.getLogger(PersoMapApplication.class);
  private final SwaggerDropwizard swaggerDropwizard = new SwaggerDropwizard();

  public static void main(String[] args) throws Exception {
    new PersoMapApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<PersoMapConfiguration> bootstrap) {
    swaggerDropwizard.onInitialize(bootstrap);
  }

  @Override
  public void run(PersoMapConfiguration configuration, Environment environment) throws Exception {
    ApplicationContext context = new FileSystemXmlApplicationContext(configuration.getBeansPath());
    LOGGER.info("Inside persoMapApplication run method. ");

    // Swagger code
    int port =
        ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory())
            .getApplicationConnectors().get(0)).getPort();
    swaggerDropwizard.onRun(configuration, environment, configuration.getSwaggerHost(), port);
    LOGGER.debug("Swagger API is running on port " + port);

    environment.jersey().register(context.getBean("persoMapResource"));

    LOGGER.info("After persoMapApplication registering ");

    // schedule job to delete stale entries
    if (configuration.isJobRun()) {
      environment
          .lifecycle()
          .scheduledExecutorService("DataCleanUpService")
          .threads(1)
          .build()
          .scheduleAtFixedRate((Runnable) context.getBean("dataCleanUpService"),
              configuration.getJobDelay(), configuration.getJobPeriod(), TimeUnit.HOURS);
    }
  }

}
