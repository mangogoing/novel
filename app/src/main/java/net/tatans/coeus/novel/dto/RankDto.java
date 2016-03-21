package net.tatans.coeus.novel.dto;

/** 排行榜实体类 */
public class RankDto {
	// id
	String _id;
	// 标题
	String title;
	// 封面
	String cover;

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

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	@Override
	public String toString() {
		return "Rank [_id=" + _id + ", title=" + title + ", cover=" + cover
				+ "]";
	}

}
