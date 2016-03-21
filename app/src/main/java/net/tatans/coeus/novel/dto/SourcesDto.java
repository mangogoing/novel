package net.tatans.coeus.novel.dto;

public class SourcesDto {
	/**
	 * 
	 */
	private String _id;
	private String source;
	private String sourceId;
	private String book;
	private String priority;
	private int chapterCount;
	private String lastChapter;
	private String updated;

	public SourcesDto(String _id, String source, String sourceId, String book,
			String priority, int chapterCount, String lastChapter,
			String updated) {
		this._id = _id;
		this.source = source;
		this.sourceId = sourceId;
		this.book = book;
		this.priority = priority;
		this.chapterCount = chapterCount;
		this.lastChapter = lastChapter;
		this.updated = updated;

	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public int getChapterCount() {
		return chapterCount;
	}

	public void setChapterCount(int chapterCount) {
		this.chapterCount = chapterCount;
	}

	public String getLastChapter() {
		return lastChapter;
	}

	public void setLastChapter(String lastChapter) {
		this.lastChapter = lastChapter;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

}
