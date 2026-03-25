package museumviewer.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class DataExchanger {
	// http client
	private static HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
	// database manager
	private static final DBManager databaseManager = new DBManager();
	// JsonObjectMapper
	private static ObjectMapper mapper = new ObjectMapper();
	// list of APIs
    // private static final String metroAPI = "https://collectionapi.metmuseum.org/public/collection/v1/objects";
    // private static final String chicagoAPI = "https://api.artic.edu/api/v1/artworks?limit=10&fields=id,title,date_display,description,dimensions,medium_display,credit_line,image_id,artist_title";
	
    public DataExchanger() {
    	if (!databaseManager.connectToDB()) {
    		System.err.println("connection to DB failed, please try again");
    	}
    }
    
    public boolean getStatus() { return DataExchanger.databaseManager.getStatus(); }
    
    public boolean requestData(int mode,int pages,String api) { // 0 - chicago, 1 - doggo.
    	if (mode == 0) {
	    	URI url = URI.create(api);
			if (url == null) {
				return false;
			}
			System.out.println(url);
	    	HttpRequest rec = HttpRequest.newBuilder().uri(url).GET().build();
	    	HttpResponse<String> res = null;
	    	try {
	    		System.out.println("Sending");
				res = client.send(rec,HttpResponse.BodyHandlers.ofString());
				System.out.println("Sent");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				return false;
			}
	    	if (res == null) {
	    		return false;
	    	}
	    	// System.out.println(res.body());
	    	JsonNode tree = mapper.readTree(res.body());
	    	int pageCount = tree.get("pagination").get("total_pages").asInt();
	    	List<ChicagoData> data = new ArrayList<ChicagoData>();
	    	JsonNode dataNode = tree.get("data");
	    	if (dataNode.isArray()) {
	    		for (JsonNode l : dataNode) {
	    			ChicagoData dat = new ChicagoData();
	    			dat.setId(l.get("id").asInt());
	    			dat.setTitle(l.get("title").asString());
	    			dat.setDate(l.get("date_display").asString());
	    			dat.setDescription(l.get("description").asString().strip());
	    			dat.setDimensions(l.get("dimensions").asString());
	    			dat.setMedium(l.get("medium_display").asString());
	    			dat.setCredit(l.get("credit_line").asString());
	    			dat.setImageId(l.get("image_id").asString());
	    			dat.setArtist(l.get("artist_title").asString());
	    			data.addLast(dat);
	    		}
	    	}
	    	// System.out.println("total_pages: ");
	    	// System.out.println(pageCount);
	    	for (ChicagoData i : data) {
	    		System.out.println(i.toString().replaceAll("<[^>]*>", "").replaceAll("\\n",""));
	    		databaseManager.postData(i.toString());
	    	}
	    	if (pages == 1 || pageCount <= 1) {
	    		return true;
	    	}
	    	if (pages > 1) {
	    		for (int i = 2; i<=pages;i++) {
		    		StringBuilder str = new StringBuilder(api);
		    		str.append("&page=");
		    		str.append(i);
		    		url = URI.create(str.toString());
		    		if (url == null) {
		    			return false;
		    		}
		    		rec = HttpRequest.newBuilder(url).GET().build();
		    		res = null;
		    		try {
						res = client.send(rec, HttpResponse.BodyHandlers.ofString());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    	if (res == null) {
			    		return false;
			    	}
			    	tree = mapper.readTree(res.body());
			    	data = new ArrayList<ChicagoData>();
			    	dataNode = tree.get("data");
			    	if (dataNode.isArray()) {
			    		for (JsonNode l : dataNode) {
			    			ChicagoData dat = new ChicagoData();
			    			dat.setId(l.get("id").asInt());
			    			dat.setTitle(l.get("title").asString());
			    			dat.setDate(l.get("date_display").asString());
			    			dat.setDescription(l.get("description").asString().strip());
			    			dat.setDimensions(l.get("dimensions").asString());
			    			dat.setMedium(l.get("medium_display").asString());
			    			dat.setCredit(l.get("credit_line").asString());
			    			dat.setImageId(l.get("image_id").asString());
			    			dat.setArtist(l.get("artist_title").asString());
			    			data.addLast(dat);
			    		}
			    	}
			    	for (ChicagoData j : data) {
			    		databaseManager.postData(j.toString());
			    	}
		    	}
		    	return true;
	    	}
	    	if (pages == 0) {
	    		for (int i = 2; i<=pageCount;i++) {
		    		StringBuilder str = new StringBuilder(api);
		    		str.append("&page=");
		    		str.append(i);
					url = URI.create(str.toString());
		    		if (url == null) {
		    			return false;
		    		}
		    		rec = HttpRequest.newBuilder(url).GET().build();
		    		res = null;
		    		try {
						res = client.send(rec, HttpResponse.BodyHandlers.ofString());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    	if (res == null) {
			    		return false;
			    	}
			    	tree = mapper.readTree(res.body());
			    	data = new ArrayList<ChicagoData>();
			    	dataNode = tree.get("data");
			    	if (dataNode.isArray()) {
			    		for (JsonNode l : dataNode) {
			    			ChicagoData dat = new ChicagoData();
			    			dat.setId(l.get("id").asInt());
			    			dat.setTitle(l.get("title").asString());
			    			dat.setDate(l.get("date_display").asString());
			    			dat.setDescription(l.get("description").asString().strip());
			    			dat.setDimensions(l.get("dimensions").asString());
			    			dat.setMedium(l.get("medium_display").asString());
			    			dat.setCredit(l.get("credit_line").asString());
			    			dat.setImageId(l.get("image_id").asString());
			    			dat.setArtist(l.get("artist_title").asString());
			    			data.addLast(dat);
			    		}
			    	}
			    	for (ChicagoData j : data) {
			    		databaseManager.postData(j.toString());
			    	}
		    	}
		    	return true;
	    	}
	    	else {
	    		return false;	    		
	    	}
    	}
    	if (mode == 1) {
    		return false;
    	}
    	else {
    		return false;
    	}
    }

    public boolean requestImage(int objectId,int mode,String cookiefile) {
    	if (mode == 0) {
    		if (!Paths.get(cookiefile).toFile().exists()) {
    			System.err.println("clearance cookie file not found");
    			return false;
    		}
    		JsonNode cookies = mapper.readTree(Paths.get(cookiefile));
    		JsonNode demo = cookies.get(".sergiodemo.com");
    		String cookie = null;
    		String uagent = null;
    		if (demo.isArray()) {
    			JsonNode req = demo.get(demo.size()-1);
    			cookie = req.get("cf_clearance").asString();
    			uagent = req.get("user_agent").asString();
    		}
    		else {
    			cookie = demo.get("cf_clearance").asString();
    			uagent = demo.get("user_agent").asString();
    		}
    		System.out.println("cf_clearance="+cookie);
    		System.out.println("Retreiving image, id = "+objectId);
    		if (cookie == null) {
    			System.err.println("malformed cookiefile");
    			return false;
    		}
    		URI url = null;
    		JsonNode tree = mapper.readTree(databaseManager.getData(objectId).toString());
    		String image_id = tree.get("image_id").asString();
    		StringBuilder str = new StringBuilder("https://www.artic.edu/iiif/2/");
    		str.append(image_id);
    		str.append("/full/843,/0/default.jpg");
    		url = URI.create(str.toString());
    		if (url == null) {
    			return false;
    		}
    		// System.out.println(url);
    		// driver.get(url.toString());
    		HttpURLConnection connection;
			try {
				connection = (HttpURLConnection) url.toURL().openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", uagent);
				connection.setRequestProperty("Cookie", "cf_clearance="+cookie);
				connection.connect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
	    	try {
	    		InputStream is = connection.getInputStream();
	    		BufferedImage img = ImageIO.read(is);
	    		File output = new File(Path.of("images/"+objectId+".jpg").toString());
	    		ImageIO.write(img,"jpg",output);
			} catch(FileNotFoundException e) {
				System.err.println("FileNotFound on server");
				e.printStackTrace();
				BufferedImage img = null;
				File output = new File(Path.of("images/"+objectId+".jpg").toString());
				try {
					img = ImageIO.read(new File(Path.of("images/nia.jpg").toString()));
		    		ImageIO.write(img,"jpg",output);
				} catch (IOException e1) {
					System.err.println("Failed to open placeholder ImageNotFound file");
					e1.printStackTrace();
					return false;
				}
				return true;
			} catch (IOException e) {
				System.err.println("Possibly cookies expired, try refreshing cookiefile "+cookiefile);
				e.printStackTrace();
				return false;
			}
	    	System.out.println("Image retrieved, id = "+objectId);
    		return true;
    	}
    	if (mode == 1) {
    		return false;
    	}
    	else {
    		return false;
    	}
    }
    public ChicagoData getData(int objectID) {
    	return databaseManager.getData(objectID);
    }
    public List<ChicagoData> getPage(int page) {
    	return databaseManager.getPage(page);
    }
}
