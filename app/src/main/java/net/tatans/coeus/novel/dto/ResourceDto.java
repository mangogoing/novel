package net.tatans.coeus.novel.dto;

public class ResourceDto {
	/**
	 * 
	 */
	private String _id;
	private String lastChapter;
	private String link;
	private String source;
	private String name;
	private boolean isCharge;
	private int chaptersCount;
	private String updated;
	private String starting;
	private String host;

	public ResourceDto(String _id, String lastChapter, String link,
			String source, String name, boolean isCharge, int chaptersCount,
			String updated, String starting, String host) {
		this._id = _id;
		this.lastChapter = lastChapter;
		this.link = link;
		this.source = source;
		this.name = name;
		this.isCharge = isCharge;
		this.chaptersCount = chaptersCount;
		this.updated = updated;
		this.starting = starting;
		this.host = host;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getLastChapter() {
		return lastChapter;
	}

	public void setLastChapter(String lastChapter) {
		this.lastChapter = lastChapter;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCharge() {
		return isCharge;
	}

	public void setCharge(boolean isCharge) {
		this.isCharge = isCharge;
	}

	public int getChaptersCount() {
		return chaptersCount;
	}

	public void setChaptersCount(int chaptersCount) {
		this.chaptersCount = chaptersCount;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getStarting() {
		return starting;
	}

	public void setStarting(String starting) {
		this.starting = starting;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	
	

}
