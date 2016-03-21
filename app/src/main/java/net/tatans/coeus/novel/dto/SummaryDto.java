package net.tatans.coeus.novel.dto;

public class SummaryDto {
	private String _id;
	private String lastChapter;
	private String link;
	private String name;
	private String source;
	private boolean isCharge;
	private int chaptersCount;
	private String updated;
	private boolean starting;
	private String host;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public boolean isStarting() {
		return starting;
	}

	public void setStarting(boolean starting) {
		this.starting = starting;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
