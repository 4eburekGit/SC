package museumviewer.server;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import junit.framework.AssertionFailedError;

class DBManagerTest {
	@Test
	void testMainDB() {
		System.out.println("Testing main database connectivity");
		DBManager testManager = new DBManager(true);
		if (!testManager.connectToDB()) {
			fail("couldn't connect to main DB");
		}
		System.out.println("Successfully connected");
	}
	@Test
	void testOtherDB() {
		System.out.println("Testing overall database connectivity");
		DBManager testManager = new DBManager(true);
		if (!testManager.connectToDB("kottsov","1417", "jdbc:postgresql://" +
				System.getenv().getOrDefault("DB_HOST", "localhost") +
				":" + System.getenv().getOrDefault("DB_PORT","5432") +
				"/scserverdata_test" )) {
			fail("couldn't connect to the test DB");
		}
		if (!testManager.getStatus()) {
			fail("invalid status");
		}
		if (!testManager.postData((new ChicagoData( // that why chicagodata tests are first
					1,
					"title",
					"date",
					"desc",
					"dim",
					"medium",
					"credit",
					"image",
					"artist"
				)).toString())) {
			fail("failed to post data");
		}
		ChicagoData data = null;
		if ((data = testManager.getData(1))!=null) {
			try {
				Assertions.assertEquals(data.getId(), 1);
				Assertions.assertEquals(data.getArtist(),"artist");
				Assertions.assertEquals(data.getImageId(),"image");
				Assertions.assertEquals(data.getCredit(),"credit");
				Assertions.assertEquals(data.getMedium(),"medium");
				Assertions.assertEquals(data.getDimensions(),"dim");
				Assertions.assertEquals(data.getDescription(),"desc");
				Assertions.assertEquals(data.getDate(),"date");
				Assertions.assertEquals(data.getTitle(),"title");
			} catch (AssertionFailedError e) {
				fail("Malformed data. Assertion failed at: "+e.getMessage());
			}
		}
		else {
			fail("failed to get data");
		}
		List<ChicagoData> testList = testManager.getPage(0);
		try {
			Assertions.assertFalse(testList.isEmpty());
		} catch (AssertionFailedError e) {
			fail("couldnt access page 0");
		}
		List<ChicagoData> testEmptyList = testManager.getPage(1);
		try {
			Assertions.assertTrue(testEmptyList.isEmpty());
		} catch (AssertionFailedError e) {
			fail("couldnt access page 1");
		}
		try {
			Assertions.assertEquals(testList.getFirst().getId(), 1);
			Assertions.assertEquals(testList.getFirst().getArtist(),"artist");
			Assertions.assertEquals(testList.getFirst().getImageId(),"image");
			Assertions.assertEquals(testList.getFirst().getCredit(),"credit");
			Assertions.assertEquals(testList.getFirst().getMedium(),"medium");
			Assertions.assertEquals(testList.getFirst().getDimensions(),"dim");
			Assertions.assertEquals(testList.getFirst().getDescription(),"desc");
			Assertions.assertEquals(testList.getFirst().getDate(),"date");
			Assertions.assertEquals(testList.getFirst().getTitle(),"title");
		} catch (AssertionFailedError e) {
			fail("Malformed data. Assertion failed at: "+e.getMessage());
		}
		if (testManager.eraseID(1)) {
			try {
				testList = testManager.getPage(0);
				Assertions.assertTrue(testList.isEmpty());
				ChicagoData dat = testManager.getData(1);
				Assertions.assertEquals(dat.getArtist(),null);
				Assertions.assertEquals(dat.getImageId(),null);
				Assertions.assertEquals(dat.getCredit(),null);
				Assertions.assertEquals(dat.getMedium(),null);
				Assertions.assertEquals(dat.getDimensions(),null);
				Assertions.assertEquals(dat.getDescription(),null);
				Assertions.assertEquals(dat.getDate(),null);
				Assertions.assertEquals(dat.getTitle(),null);
			} catch (AssertionFailedError e) {
				fail("Failed deletion, assertion failed: "+e.toString());
			}
		}
		else {
			fail("Failed to delete");
		}
		System.out.println("Data insertion, recovery are successful");
	}
}
