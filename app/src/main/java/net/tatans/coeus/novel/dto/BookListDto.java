package net.tatans.coeus.novel.dto;

public class BookListDto {
	private String _id;
	private String title;
	private String author;
	private String shortIntro;
	private String cover;
	private String site;
	private String cat;
	private String latelyFollower;
	private String retentionRatio;
	private String lastChapter;
	private String tags;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getShortIntro() {
		return shortIntro;
	}

	public void setShortIntro(String shortIntro) {
		this.shortIntro = shortIntro;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getLatelyFollower() {
		return latelyFollower;
	}

	public void setLatelyFollower(String latelyFollower) {
		this.latelyFollower = latelyFollower;
	}

	public String getRetentionRatio() {
		return retentionRatio;
	}

	public void setRetentionRatio(String retentionRatio) {
		this.retentionRatio = retentionRatio;
	}

	public String getLastChapter() {
		return lastChapter;
	}

	public void setLastChapter(String lastChapter) {
		this.lastChapter = lastChapter;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

}
