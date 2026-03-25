package museumviewer.server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.sun.net.httpserver.HttpServer;

public class Main {
	private static HttpServer server;
	public static void main(String[] args) {
		System.out.println("Server is setting up");
		DataExchanger exc = new DataExchanger();
		if (!exc.getStatus()) {
			return;
		}
		int serverPort = 8000;
		try {
			server = HttpServer.create(new InetSocketAddress(serverPort), 0);
		} catch (IOException e) {
			System.err.println("Failed to launch API server");
			e.printStackTrace();
			return;
		}
	    server.createContext("/api/server", (exchange -> {
		    if (exchange.getRequestMethod().equals("GET")) {// Normal GET params: (OP/RQ) id = int & (RQ) mode = boolean(int) & (OP) page=int | returns image (encoded) and description 
		    	Map<String,List<String>> parsedParams = parseQuery(exchange.getRequestURI().toString());
		    	if (parsedParams.isEmpty()) {
		    		String respText = "No params provided, returning nothing!\n";
		    		System.out.println(exchange.getRequestURI());
		    	    exchange.sendResponseHeaders(400, respText.getBytes().length);
		    	    OutputStream output = exchange.getResponseBody();
		    	    output.write(respText.getBytes());
		    	    output.flush();
		    	} // Returns jpg image or first 100-element page of elements
		    	else {
		    		if (!parsedParams.containsKey("mode")) {
		    			String respText = "No mode provided, returning nothing!\n";
			    	    exchange.sendResponseHeaders(400, respText.getBytes().length);
			    	    OutputStream output = exchange.getResponseBody();
			    	    output.write(respText.getBytes());
			    	    output.flush();
		    		}
		    		else if (!parsedParams.containsKey("id")) {
		    			if (Integer.valueOf(parsedParams.get("mode").getFirst()).equals(1)) {
		    				StringBuilder respText = new StringBuilder("{ \"data\" : [ ");
			    			List<ChicagoData> page = null;
			    			if (!parsedParams.containsKey("page")) {
			    				page = exc.getPage(0);
			    				boolean flag = false;
				    			for (ChicagoData i : page) {
				    				if (flag) {
				    					respText.append(",");
				    				}
				    				else flag = true;
				    				respText.append("{ \"id\" : ");
				    				respText.append(i.getId());
				    				respText.append(",\"title\" : ");
				    				if (i.getTitle().isEmpty()) {
				    					respText.append("\"\"");
				    				}
				    				else {
				    					respText.append("\"" + i.getTitle() + "\"");
				    				}
				    				respText.append(",\"artist\" : ");
				    				if (i.getArtist().isEmpty()) {
				    					respText.append("\"\"");
				    				}
				    				else {
				    					respText.append("\"" + i.getArtist() + "\"");
				    				}
				    				respText.append(",\"description\" : ");
				    				if (i.getDescription().isEmpty()) {
				    					respText.append("\"\"");
				    				}
				    				else {
				    					respText.append("\""+i.getDescription()+"\"");
				    				}
				    				respText.append("}");
				    			}
				    			respText.append("] }");
				    			String response = respText.toString(); 
				    			exchange.sendResponseHeaders(200, response.getBytes().length);
					    	    OutputStream output = exchange.getResponseBody();
					    	    output.write(response.getBytes());
					    	    output.flush();
			    			}
			    			else if (Integer.valueOf(parsedParams.get("page").toString()) < 0) {
			    				String response = "Invalid page number (must be non-negative)!\n";
					    	    exchange.sendResponseHeaders(400, response.getBytes().length);
					    	    OutputStream output = exchange.getResponseBody();
					    	    output.write(response.getBytes());
					    	    output.flush();
			    			}
			    			else {
			    				page = exc.getPage(Integer.valueOf(parsedParams.get("page").toString()));
				    			boolean flag = false;
				    			for (ChicagoData i : page) {
				    				if (flag) {
				    					respText.append(",");
				    				}
				    				else flag = true;
				    				respText.append("{ \"id\" : ");
				    				respText.append(i.getId());
				    				respText.append(",\"title\" : ");
				    				if (i.getTitle().isEmpty()) {
				    					respText.append("\"\"");
				    				}
				    				else {
				    					respText.append("\"" + i.getTitle() + "\"");
				    				}
				    				respText.append(",\"artist\" : ");
				    				if (i.getArtist().isEmpty()) {
				    					respText.append("\"\"");
				    				}
				    				else {
				    					respText.append("\"" + i.getArtist() + "\"");
				    				}
				    				respText.append(",\"description\" : ");
				    				if (i.getDescription().isEmpty()) {
				    					respText.append("\"\"");
				    				}
				    				else {
				    					respText.append("\""+i.getDescription()+"\"");
				    				}
				    				respText.append("}");
				    			}
				    			respText.append("] }");
				    			String response = respText.toString(); 
				    			exchange.sendResponseHeaders(200, response.getBytes().length);
					    	    OutputStream output = exchange.getResponseBody();
					    	    output.write(response.getBytes());
					    	    output.flush();
			    			}
		    			}
		    			else if (Integer.valueOf(parsedParams.get("mode").getFirst()).equals(0)) {
			    			String respText = "No objectID provided with mode 0, returning nothing!\n";
				    	    exchange.sendResponseHeaders(400, respText.getBytes().length);
				    	    OutputStream output = exchange.getResponseBody();
				    	    output.write(respText.getBytes());
				    	    output.flush();
		    			}
		    			else {
		    				String respText = "Invalid mode, valid ones are 0/1, returning nothing!\n";
				    	    exchange.sendResponseHeaders(400, respText.getBytes().length);
				    	    OutputStream output = exchange.getResponseBody();
				    	    output.write(respText.getBytes());
				    	    output.flush();
		    			}
		    		}
		    		else {
		    			int objectID = Integer.valueOf(parsedParams.get("id").getFirst());
		    			System.out.println(objectID);
		    			File imgfile = new File(Path.of("images/"+objectID+".jpg").toString());
		    			if (!imgfile.exists()) {
		    				System.err.println("File "+objectID+" not found,requesting");
		    				if (!exc.requestImage(objectID, 0, "cookies.json")) {
		    					System.err.println("File "+objectID+" could not be requested");
		    					String respText = "File "+objectID+" could not be requested\n";
					    	    exchange.sendResponseHeaders(503, respText.getBytes().length);
					    	    OutputStream output = exchange.getResponseBody();
					    	    output.write(respText.getBytes());
					    	    output.flush();
		    				}
		    				else {
		    					System.out.println(Path.of("images/"+objectID+".jpg").toString());
				    			try {
				    				BufferedImage img = ImageIO.read(imgfile);
				    				exchange.sendResponseHeaders(200, imgfile.getTotalSpace());
				    				OutputStream output = exchange.getResponseBody();
				    				ImageIO.write(img, "jpg", output);
				    				output.flush();
				    			} catch(IOException e) {
				    				System.err.println(e.toString());
				    				String errstr = "Failed to load image "+objectID+".jpg";
				    				exchange.sendResponseHeaders(503, errstr.getBytes().length);
				    				OutputStream output = exchange.getResponseBody();
						    	    output.write(errstr.getBytes());
						    	    output.flush();
				    			}
		    				}
		    			}
		    			else {
		    				System.out.println(Path.of("images/"+objectID+".jpg").toString());
			    			try {
			    				BufferedImage img = ImageIO.read(imgfile);
			    				exchange.sendResponseHeaders(200, imgfile.getTotalSpace());
			    				OutputStream output = exchange.getResponseBody();
			    				ImageIO.write(img, "jpg", output);
			    				output.flush();
			    			} catch(IOException e) {
			    				System.err.println(e.toString());
			    				String errstr = "Failed to load image "+objectID+".jpg";
			    				exchange.sendResponseHeaders(503, errstr.getBytes().length);
			    				OutputStream output = exchange.getResponseBody();
					    	    output.write(errstr.getBytes());
					    	    output.flush();
			    			}
		    			}
		    		}
		    	}
		    }
		    else {
		    	exchange.sendResponseHeaders(405, -1); // 405 op not allowed
		    }
		    exchange.close();
		}
	    ));
	    server.setExecutor(null); // creates a default executor
	    server.start();
	    System.out.println("Server started");
	    Scanner inScan = new Scanner(System.in);
	}
	public static void shutdown() {
		server.stop(0);
	}
	
	public static Map<String,List<String>> parseQuery(String query) {
		if (query.isEmpty() || query == null) {
			return Collections.emptyMap();
		}
		String[] paramsUnsplit = Pattern.compile("\\?").split(query,0);
		if (paramsUnsplit.length == 0 || paramsUnsplit[0].equals(query)) { return Collections.emptyMap(); }
		String[] params = null;
		if (paramsUnsplit.length == 2) {
			params = paramsUnsplit[1].split("&");
		}
		else { return Collections.emptyMap(); }
 		Map<String,List<String>> retval = new HashMap<String,List<String>>();
		for (String i : params) {
			String[] paramValues = i.split("=");
			if (paramValues.length != 2) {
				System.err.println("Failed to parse the params, possible malformed request");
				System.err.println("Caused by: "+i);
				return Collections.emptyMap();
			}
			retval.put(paramValues[0], List.of(paramValues[1].split(",")));
		}
		return retval;
	}
}
