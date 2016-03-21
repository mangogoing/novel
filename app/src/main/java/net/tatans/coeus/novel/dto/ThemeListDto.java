package net.tatans.coeus.novel.dto;

/** 主题书籍下一级目录书籍介绍 */
public class ThemeListDto {
	String _id;
	String author;
	String cover;
	String title;
	String site;
	String wordCount;
	String banned;
	String latelyFollower;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getWordCount() {
		return wordCount;
	}

	public void setWordCount(String wordCount) {
		this.wordCount = wordCount;
	}

	public String getBanned() {
		return banned;
	}

	public void setBanned(String banned) {
		this.banned = banned;
	}

	public String getLatelyFollower() {
		return latelyFollower;
	}

	public void setLatelyFollower(String latelyFollower) {
		this.latelyFollower = latelyFollower;
	}

	public ThemeListDto(String _id, String author, String cover,
			String title, String site, String wordCount, String banned,
			String latelyFollower) {
		super();
		this._id = _id;
		this.author = author;
		this.cover = cover;
		this.title = title;
		this.site = site;
		this.wordCount = wordCount;
		this.banned = banned;
		this.latelyFollower = latelyFollower;
	}

	@Override
	public String toString() {
		return "BooksThemeListDto [_id=" + _id + ", author=" + author
				+ ", cover=" + cover + ", title=" + title + ", site=" + site
				+ ", wordCount=" + wordCount + ", banned=" + banned
				+ ", latelyFollower=" + latelyFollower + "]";
	}
}
