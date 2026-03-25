package museumviewer.server;

public class ChicagoData {
	// fields=id,title,date_display,description,dimensions,medium_display,credit_line,image_id,artist_title
	private int id;
	private String title;
	private String date_display;
	private String description;
	private String dimensions;
	private String medium_display;
	private String credit_line;
	private String image_id;
	private String artist_title;
	
	public ChicagoData(int id,
			String title,
			String date_display,
			String description,
			String dimensions,
			String medium_display,
			String credit_line,
			String image_id,
			String artist_title) {
		this.id = id;
		this.title = title;
		this.date_display = date_display;
		this.description = description;
		this.dimensions = dimensions;
		this.medium_display = medium_display;
		this.credit_line = credit_line;
		this.image_id = image_id;
		this.artist_title = artist_title;
	}
	
	public ChicagoData() {
		this.id = -1;
		this.title = null;
		this.date_display = null;
		this.description = null;
		this.dimensions = null;
		this.medium_display = null;
		this.credit_line = null;
		this.image_id = null;
		this.artist_title = null;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("{\"id\": ");
		str.append(this.id);
		str.append(",\"title\": ");
		if (this.title == null) {
			str.append("null");
		} else {
			str.append("\""+this.title.replaceAll("\"","")+"\"");
		}
		str.append(",\"date_display\": ");
		if (this.date_display == null) {
			str.append("null");
		} else {
			str.append("\""+this.date_display+"\"");
		}
		str.append(",\"description\": ");
		if (this.description == null) {
			str.append("null");
		} else {
			str.append("\""+this.description.replaceAll("\"","")+"\"");
		}
		str.append(",\"dimensions\": ");
		if (this.dimensions == null) {
			str.append("null");
		} else {
			str.append("\""+this.dimensions.replaceAll("\"","")+"\"");
		}
		str.append(",\"medium_display\": ");
		if (this.medium_display == null) {
			str.append("null");
		} else {
			str.append("\""+this.medium_display.replaceAll("\"","")+"\"");
		}
		str.append(",\"credit_line\": ");
		if (this.credit_line == null) {
			str.append("null");
		} else {
			str.append("\""+this.credit_line.replaceAll("\"","")+"\"");
		}
		str.append(",\"image_id\": ");
		if (this.image_id == null) {
			str.append("null");
		} else {
			str.append("\""+this.image_id+"\"");
		}
		str.append(",\"artist_title\": ");
		if (this.artist_title == null) {
			str.append("null");
		} else {
			str.append("\""+this.artist_title.replaceAll("\"","")+"\"");
		}
		str.append("}");
		return str.toString();
	}
	
	public int getId() { return this.id; }
	public void setId(int id) { this.id = id; }
	public String getTitle() { return this.title; }
	public void setTitle(String title) { this.title = title; }
	public String getDate() { return this.date_display; }
	public void setDate(String date_display) { this.date_display = date_display; }
	public String getDescription() { return this.description; }
	public void setDescription(String description) { this.description = description; }
	public String getDimensions() { return this.dimensions; }
	public void setDimensions(String dimensions) { this.dimensions = dimensions; }
	public String getMedium() { return this.medium_display; }
	public void setMedium(String medium_display) { this.medium_display = medium_display; }
	public String getCredit() { return this.credit_line; }
	public void setCredit(String credit_line) { this.credit_line = credit_line; }
	public String getImageId() { return this.image_id; }
	public void setImageId(String image_id) { this.image_id = image_id; }
	public String getArtist() { return this.artist_title; }
	public void setArtist(String artist_title) { this.artist_title = artist_title; }	
}
