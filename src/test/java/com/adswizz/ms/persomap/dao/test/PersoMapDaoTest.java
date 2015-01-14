// Copyright 2014 AdsWizz Inc. All Rights Reserved

package com.adswizz.ms.persomap.dao.test;

import static com.adswizz.ms.persomap.util.PersomapUtil.SEPARATOR;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.adswizz.ms.persomap.config.PersoMapConfiguration;
import com.adswizz.ms.persomap.dao.PersoMapDao;
import com.adswizz.ms.persomap.dao.PersoMapDaoImpl;
import com.adswizz.ms.persomap.model.PersoMapUser;
import com.adswizz.ms.persomap.util.PersomapUtil;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

/**
 * Test class for {@link PersoMapDao}
 * 
 * @author AdsWizz Inc.
 * 
 */
public class PersoMapDaoTest {

	private static PersoMapDao persoMapDao;
	private static AmazonDynamoDBClient amazonDynamoDBClient;
	private static long readCapacityUnites = 10;
	private static long writeCapacityUnits = 10;
	private static final String dynamoDB = System.getProperty("dynamodb");
	private static Process process = null;
	private static final String TABLE_NAME = "develDynamoTable";

	@BeforeClass
	public static void setUp() throws Exception {

		String filePath = dynamoDB + "DynamoDBLocal.jar";
		Runtime runtime = Runtime.getRuntime();
		process = runtime.exec("java -jar " + filePath);

		// setting Amazon DB client parameters.
		amazonDynamoDBClient = new AmazonDynamoDBClient(
				new BasicAWSCredentials("fake", "fake"));

		// TODO: remove this. used for local testing
		amazonDynamoDBClient.setEndpoint("http://localhost:8000", "dynamodb",
				"local");

		// for AWS web service
		// amazonDynamoDBClient.setEndpoint(configuration.getServiceName());
		DynamoDBMapper dynamoDbMapper = new DynamoDBMapper(amazonDynamoDBClient);
		PersoMapConfiguration configuration = new PersoMapConfiguration();
		configuration.setDynamoDBTable(TABLE_NAME);
		configuration.setMaxRetainDays(0);

		persoMapDao = new PersoMapDaoImpl(dynamoDbMapper,
				configuration.getMaxRetainDays());

		createPersoMapTable(amazonDynamoDBClient, configuration);
	}

	@AfterClass
	public static void tearDown() {
		DeleteTableResult result = amazonDynamoDBClient.deleteTable(TABLE_NAME);
		process.destroy();
		System.out.println(result.getTableDescription().getTableName()
				+ " deleted");
	}

	/**
	 * Method creates PersoMap table in local DynamoDB instance for testing
	 * 
	 * @param dynamoDB
	 */
	public static void createPersoMapTable(
			AmazonDynamoDBClient amazonDynamoDBClient,
			PersoMapConfiguration configuration) {
		try {
			CreateTableRequest request = new CreateTableRequest()
					.withTableName(configuration.getDynamoDBTable());
			request.withKeySchema(new KeySchemaElement()
					.withAttributeName("id").withKeyType(KeyType.HASH));
			request.withAttributeDefinitions(new AttributeDefinition()
					.withAttributeName("id").withAttributeType(
							ScalarAttributeType.S));

			ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(
					readCapacityUnites, writeCapacityUnits);
			request.setProvisionedThroughput(provisionedThroughput);

			amazonDynamoDBClient.createTable(request);
		} catch (Exception ex) {
			throw new RuntimeException("Error in creating table");
		}
	}

	@Test
	public void testsetUserMapping() {
		persoMapDao.setUserMapping("aws1", "p1", "www.google.com", "COOKIE");
		String partnerWithIdKey = persoMapDao.getPartnerUserId("aws1",
				"www.google.com", "COOKIE");
		String partnerId = partnerWithIdKey.split(SEPARATOR)[1];
		Assert.assertEquals("p1", partnerId);
	}

	@Test
	public void testgetPartnerUserId() {
		persoMapDao.setUserMapping("aws2", "p2", "www.yahoo.com", "COOKIE");
		String partnerWithIdKey = persoMapDao.getPartnerUserId("aws2",
				"www.yahoo.com", "COOKIE");
		String partnerId = partnerWithIdKey.split(SEPARATOR)[1];
		Assert.assertEquals("p2", partnerId);
	}

	@Test
	public void testgetAdsWizzUserId() {
		persoMapDao.setUserMapping("aws3", "p3", "www.amazon.com", "COOKIE");
		String adsWizzUserIdWithKey = persoMapDao.getAdsWizzUserId("p3",
				"www.amazon.com", "COOKIE");
		String adsWizzUserId = adsWizzUserIdWithKey.split(SEPARATOR)[1];
		Assert.assertEquals("aws3", adsWizzUserId);
	}

	@Test
	public void testupdateLastSeenDate() {
		persoMapDao.setUserMapping("aws4", "p4", "www.amazon.com", "COOKIE");
		String adsWizzUserIdWithKey = persoMapDao.getAdsWizzUserId("p4",
				"www.amazon.com", "COOKIE");
		String key = adsWizzUserIdWithKey.split(SEPARATOR)[0];
		persoMapDao.updateLastSeenDate(key);
		adsWizzUserIdWithKey = persoMapDao.getAdsWizzUserId("p4",
				"www.amazon.com", "COOKIE");
		String date = adsWizzUserIdWithKey.split(SEPARATOR)[2];
		Assert.assertEquals(PersomapUtil.getCurrentDate(), date);
	}

	@Test
	public void testdeleteUserMappings() {
		persoMapDao.setUserMapping("adsWizzUserId5", "partnerUserId5",
				"www.msn.com", "COOKIE");
		List<PersoMapUser> deletedList = persoMapDao.deleteUserMappings();
		String adsWizzUserIdWithKey = persoMapDao.getAdsWizzUserId("partnerUserId5", "www.msn.com", "COOKIE");
		Assert.assertNull(adsWizzUserIdWithKey);
	}

}
