package museumviewer.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import junit.framework.AssertionFailedError;

class ChicagoDataTest {
	@Test
	void test() {
		ChicagoData dataDefault = new ChicagoData();
		ChicagoData dataSomething = new ChicagoData(1,"data1","data2","data3","data4","data5","data6","data7","data8");
		try {
			Assertions.assertEquals(dataSomething.getId(),1);
			Assertions.assertEquals(dataSomething.getTitle(),"data1");
			Assertions.assertEquals(dataSomething.getDate(),"data2");
			Assertions.assertEquals(dataSomething.getDescription(),"data3");
			Assertions.assertEquals(dataSomething.getDimensions(),"data4");
			Assertions.assertEquals(dataSomething.getMedium(),"data5");
			Assertions.assertEquals(dataSomething.getCredit(),"data6");
			Assertions.assertEquals(dataSomething.getImageId(),"data7");
			Assertions.assertEquals(dataSomething.getArtist(),"data8");
		} catch (AssertionFailedError e) {
			fail("Assertion failed at : "+e.toString());
		}
		try {
			Assertions.assertEquals(dataDefault.getId(),-1);
			Assertions.assertNull(dataDefault.getTitle());
			Assertions.assertNull(dataDefault.getDate());
			Assertions.assertNull(dataDefault.getDescription());
			Assertions.assertNull(dataDefault.getDimensions());
			Assertions.assertNull(dataDefault.getMedium());
			Assertions.assertNull(dataDefault.getCredit());
			Assertions.assertNull(dataDefault.getImageId());
			Assertions.assertNull(dataDefault.getArtist());
		} catch (AssertionFailedError e) {
			fail("Assertion failed at : "+e.toString());
		}
		try {
			Assertions.assertEquals(dataSomething.toString(), "{\"id\": 1,\"title\": \"data1\",\"date_display\": \"data2\",\"description\": \"data3\",\"dimensions\": \"data4\",\"medium_display\": \"data5\",\"credit_line\": \"data6\",\"image_id\": \"data7\",\"artist_title\": \"data8\"}");
			Assertions.assertEquals(dataDefault.toString(), "{\"id\": -1,\"title\": null,\"date_display\": null,\"description\": null,\"dimensions\": null,\"medium_display\": null,\"credit_line\": null,\"image_id\": null,\"artist_title\": null}");
		} catch (AssertionFailedError e) {
			fail("Assertion failed at : "+e.toString());
		}
		dataSomething.setId(2);
		dataSomething.setTitle("data2");
		dataSomething.setDate("data1");
		dataSomething.setDescription("data2");
		dataSomething.setDimensions("data2");
		dataSomething.setMedium("data2");
		dataSomething.setCredit("data2");
		dataSomething.setImageId("data2");
		dataSomething.setArtist("data2");
		try {
			Assertions.assertEquals(dataSomething.getId(),2);
			Assertions.assertEquals(dataSomething.getTitle(),"data2");
			Assertions.assertEquals(dataSomething.getDate(),"data1");
			Assertions.assertEquals(dataSomething.getDescription(),"data2");
			Assertions.assertEquals(dataSomething.getDimensions(),"data2");
			Assertions.assertEquals(dataSomething.getMedium(),"data2");
			Assertions.assertEquals(dataSomething.getCredit(),"data2");
			Assertions.assertEquals(dataSomething.getImageId(),"data2");
			Assertions.assertEquals(dataSomething.getArtist(),"data2");
		} catch (AssertionFailedError e) {
			fail("Assertion failed at : "+e.toString());
		}
	}

}
