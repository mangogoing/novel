package net.tatans.coeus.novel.dto;

/** 子排行榜实体类 */
public class RankListDto {
	String _id;
	String author;
	String cover;
	//简介
	String shortIntro;
	String site;
	String title;
	String banned;
	//正在追书人数
	String latelyFollower;
	String retentionRatio;

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

	public String getShortIntro() {
		return shortIntro;
	}

	public void setShortIntro(String shortIntro) {
		this.shortIntro = shortIntro;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getRetentionRatio() {
		return retentionRatio;
	}

	@Override
	public String toString() {
		return "SubRankingList [_id=" + _id + ", author=" + author + ", cover="
				+ cover + ", shortIntro=" + shortIntro + ", site=" + site
				+ ", title=" + title + ", banned=" + banned
				+ ", latelyFollower=" + latelyFollower + ", retentionRatio="
				+ retentionRatio + "]";
	}

	public void setRetentionRatio(String retentionRatio) {
		this.retentionRatio = retentionRatio;
	}

}
