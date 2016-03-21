package net.tatans.coeus.novel.dto;

/** 主题书籍 **/
public class ThemeDto {
	String _id;
	String title;
	//最近收藏人数
	String collectorCount;
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

	public String getCollectorCount() {
		return collectorCount;
	}

	public void setCollectorCount(String collectorCount) {
		this.collectorCount = collectorCount;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result
				+ ((collectorCount == null) ? 0 : collectorCount.hashCode());
		result = prime * result + ((cover == null) ? 0 : cover.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThemeDto other = (ThemeDto) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (collectorCount == null) {
			if (other.collectorCount != null)
				return false;
		} else if (!collectorCount.equals(other.collectorCount))
			return false;
		if (cover == null) {
			if (other.cover != null)
				return false;
		} else if (!cover.equals(other.cover))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public ThemeDto(String _id, String title, String collectorCount,
			String cover) {
		super();
		this._id = _id;
		this.title = title;
		this.collectorCount = collectorCount;
		this.cover = cover;
	}

	@Override
	public String toString() {
		return "BooksPublicDto [_id=" + _id + ", title=" + title
				+ ", collectorCount=" + collectorCount + ", cover=" + cover
				+ "]";
	}

}
