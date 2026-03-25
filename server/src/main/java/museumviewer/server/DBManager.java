package museumviewer.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class DBManager {
	// JSON mapper
	private static ObjectMapper mapper = new ObjectMapper();
	// main db connection data
	private static final String DB_URL = "jdbc:postgresql://" +
	        System.getenv().getOrDefault("DB_HOST", "localhost") +
			":" + System.getenv().getOrDefault("DB_PORT","5432") +
			"/scserverdata";
    private static final String DB_USER = "kottsov";
    private static final String DB_PASSWORD = "1417";
    
    // connection flag
    private boolean connected = false;
    public boolean getStatus() { return this.connected; }
    
    // connection
    private Connection conn;
    
    public DBManager() {
    	this.connected = connectToDB();
    }
    
    public DBManager(boolean dontConnect) {
    	if (!dontConnect) {
    		this.connected = connectToDB();
    	}
    }
    
    public DBManager(String user,String password, String URL) {
    	this.connected = connectToDB(user,password,URL);
    }
    
	public boolean connectToDB() {
		Properties props = new Properties();
		props.setProperty("user", DB_USER);
		props.setProperty("password", DB_PASSWORD);
		conn = null;
		try {
			conn = DriverManager.getConnection(DB_URL, props);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (conn == null) {
			return false;
		}
		this.connected = true;
		return true;
	}
	public boolean connectToDB(String user,String password, String URL) {
		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		conn = null;
		try {
			conn = DriverManager.getConnection(URL, props);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (conn == null) {
			return false;
		}
		this.connected = true;
		return true;
	}
	
	public boolean postData(String JSONFileData) {
		if (!this.connected) {
			return false;
		}
		JsonNode tree = null;
		String queryText = "INSERT INTO chicagodata (id,title,date_display,description,dimensions,medium_display,credit_line,image_id,artist_title,download_date,last_access_date) VALUES (?,?,?,?,?,?,?,?,?,localtimestamp(0),localtimestamp(0))";
		PreparedStatement statement = null;
		String FormattedData = JSONFileData.replaceAll("<[^>]*>", "").replaceAll("\\n","");
		try {
			tree = mapper.readTree(FormattedData);
		}
		catch (StreamReadException e) {
			System.err.println(FormattedData);
			System.err.println("Malformed JSON");
			System.err.println(e.toString());
			return false;
		}
		try {
			statement = conn.prepareStatement(queryText);
			statement.setInt(1,tree.get("id").asInt());
			if (tree.get("title").isNull()) {
				statement.setNull(2,Types.VARCHAR);
			} else {
				statement.setString(2,tree.get("title").asString());
			}
			if (tree.get("date_display").isNull()) {
				statement.setNull(3,Types.VARCHAR);
			} else {
				statement.setString(3,tree.get("date_display").asString());
			}
			if (tree.get("description").isNull()) {
				statement.setNull(4,Types.VARCHAR);
			} else {
				statement.setString(4,tree.get("description").asString());
			}
			if (tree.get("dimensions").isNull()) {
				statement.setNull(5,Types.VARCHAR);
			} else {
				statement.setString(5,tree.get("dimensions").asString());
			}
			if (tree.get("medium_display").isNull()) {
				statement.setNull(6,Types.VARCHAR);
			} else {
				statement.setString(6,tree.get("medium_display").asString());
			}
			if (tree.get("credit_line").isNull()) {
				statement.setNull(7,Types.VARCHAR);
			} else {
				statement.setString(7,tree.get("credit_line").asString());
			}
			if (tree.get("image_id").isNull()) {
				statement.setNull(8,Types.VARCHAR);
			} else {
				statement.setString(8,tree.get("image_id").asString());
			}
			if (tree.get("artist_title").isNull()) {
				statement.setNull(9,Types.VARCHAR);
			} else {
				statement.setString(9,tree.get("artist_title").asString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		try {
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public ChicagoData getData(int id) {
		if (!this.connected) {
			return null;
		}
		String queryText = "SELECT * FROM chicagodata WHERE id = ?";
		PreparedStatement statement = null;
		PreparedStatement update = null;
		try {
			statement = conn.prepareStatement(queryText);
			statement.setInt(1,id);
			update = conn.prepareStatement("UPDATE chicagodata SET last_access_date = localtimestamp(0) WHERE id = ?");
			update.setInt(1, id);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		ChicagoData record = new ChicagoData();
		try {
			ResultSet rs = statement.executeQuery();
			if (rs.isLast()) {
				return null; 
			}
			else if (rs.next()) {
				record.setId(rs.getInt("id"));
				record.setTitle(rs.getString("title"));
				record.setDate(rs.getString("date_display"));
				record.setDescription(rs.getString("description"));
				record.setDimensions(rs.getString("dimensions"));
				record.setMedium(rs.getString("medium_display"));
				record.setCredit(rs.getString("credit_line"));
				record.setImageId(rs.getString("image_id"));
				record.setArtist(rs.getString("artist_title"));
				update.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return record;
	}
	@SuppressWarnings("deprecation") // because SQL timestamp
	private boolean assessTime(int objectId) { // true if it's overdue for deletion, false otherwise
		if (!this.connected) {
			return false;
		}
		String queryText = "SELECT last_access_date FROM chicagodata WHERE id = ?";
		PreparedStatement statement = null;
		PreparedStatement control = null;
		try {
			statement = conn.prepareStatement(queryText);
			control = conn.prepareStatement("SELECT localtimestamp(0)");
			statement.setInt(1,objectId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				Timestamp last_accessed = rs.getTimestamp("last_access_date");
				Timestamp servertime = control.executeQuery().getTimestamp("localtimestamp");
				last_accessed.setYear(last_accessed.getYear()+5);
				if (servertime.after(last_accessed)) {
					return true;
				}
				else { return false; }
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	public boolean vacuumLeastAccessed() {
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("SELECT id FROM chicagodata");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		try {
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				if (assessTime(rs.getInt("id"))) {
					try {
						Files.delete(Path.of("images/"+rs.getInt("id")+".jpg"));
					} catch (IOException e) {
						System.err.println("Couldnt vacuum file "+rs.getInt("id")+".jpg");
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public List<ChicagoData> getPage(int page) {
		PreparedStatement statement = null;
		List<ChicagoData> retval = new ArrayList<ChicagoData>();
		try {
			statement = conn.prepareStatement("SELECT * FROM chicagodata ORDER BY id ASC OFFSET "+100*page+" LIMIT 100");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				ChicagoData record = new ChicagoData();
				record.setId(rs.getInt("id"));
				record.setTitle(rs.getString("title"));
				record.setDate(rs.getString("date_display"));
				record.setDescription(rs.getString("description"));
				record.setDimensions(rs.getString("dimensions"));
				record.setMedium(rs.getString("medium_display"));
				record.setCredit(rs.getString("credit_line"));
				record.setImageId(rs.getString("image_id"));
				record.setArtist(rs.getString("artist_title"));
				retval.add(record);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return retval;
	}
	public boolean eraseID(int id) {
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("DELETE FROM chicagodata WHERE id = "+id);
		} catch (SQLException e) {
			System.err.println("Malformed id "+id+" due to:");
			e.printStackTrace();
			return false;
		}
		try {
			statement.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to delete id "+id+" due to:");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
