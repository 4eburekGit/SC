package museumviewer.server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import junit.framework.AssertionFailedError;

class MainTest {

	private static HttpClient client;
	private static String uri_local;
	
	@BeforeAll
	static void setUpBeforeClass() {
		client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
		uri_local = "http://localhost:8000/api/server";
	}

	@AfterAll
	static void tearDownAfterClass() {
		client.shutdown();
	}

	@Test
	void test() {
		Map<String,List<String>> parsed = Main.parseQuery("localhost:8000/api/server?id=1&param=2");
		try {
			Assertions.assertTrue(parsed.containsKey("id"));
			Assertions.assertEquals(Integer.valueOf(parsed.get("id").getFirst()),1);
			Assertions.assertTrue(parsed.containsKey("param"));
			Assertions.assertEquals(Integer.valueOf((parsed.get("param").getFirst())),2);
		}
		catch (AssertionFailedError e) {
			fail("Failed to parse parameters properly");
		}
		Thread mainThread = new Thread() {
			public void run() {
				Main.main(null);
			}
		};
		mainThread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		URI uri1 = URI.create(uri_local);
		URI uri2 = URI.create(uri_local+"?mode=0");
		URI uri3 = URI.create(uri_local+"?mode=2");
		URI uri4 = URI.create(uri_local+"?mode=1");
		URI uri5 = URI.create(uri_local+"?mode=0&id=24202");
		HttpRequest rec1 = HttpRequest.newBuilder().uri(uri1).GET().build();
		HttpRequest rec2 = HttpRequest.newBuilder().uri(uri2).GET().build();
		HttpRequest rec3 = HttpRequest.newBuilder().uri(uri3).GET().build();
		HttpRequest rec4 = HttpRequest.newBuilder().uri(uri4).GET().build();
		HttpRequest rec5 = HttpRequest.newBuilder().uri(uri5).GET().build();
    	HttpResponse<String> res1 = null;
    	HttpResponse<String> res2 = null;
    	HttpResponse<String> res3 = null;
    	HttpResponse<String> res4 = null;
    	HttpResponse<InputStream> res5 = null;
    	try {
			res1 = client.send(rec1,HttpResponse.BodyHandlers.ofString());
			res2 = client.send(rec2,HttpResponse.BodyHandlers.ofString());
			res3 = client.send(rec3,HttpResponse.BodyHandlers.ofString());
			res4 = client.send(rec4,HttpResponse.BodyHandlers.ofString());
			res5 = client.send(rec5,HttpResponse.BodyHandlers.ofInputStream());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
    	try {
    		Assertions.assertEquals(res1.body(),"No params provided, returning nothing!\n");
    		Assertions.assertEquals(res2.body(),"No objectID provided with mode 0, returning nothing!\n");
    		Assertions.assertEquals(res3.body(),"Invalid mode, valid ones are 0/1, returning nothing!\n");
    		Assertions.assertEquals(res4.statusCode(),200);
    		Assertions.assertEquals(res5.statusCode(),200);
    	} catch (AssertionFailedError e) {
    		fail("Assertion failed on Main: "+ e.getMessage());
    	}
    	Main.shutdown();
    	try {
			mainThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
