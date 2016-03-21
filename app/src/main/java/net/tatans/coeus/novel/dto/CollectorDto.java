package net.tatans.coeus.novel.dto;

import net.tatans.coeus.annotation.sqlite.Table;

import java.util.Date;

/** 我的收藏 */
@Table(name = "MyCollector")
public class CollectorDto {
	public CollectorDto() {
		// TODO Auto-generated constructor stub
	}

	// 小说id
	private String _id;
	// 小说名字
	private String title;
	// 保存的章節位置
	private int savedPosition;
	/**
	 * -2未收藏，-1为收藏   0为正在下载     1为已下载     2为等待列队下载,3本地小说
	 */
	private int isDownLoad;
	// 保存的时间
	private Date date;
	// 章节总数
	private int chaperCount;

	private int countPage;

	private int sentenceIndex;

	private int position;
	private String source;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getChaperCount() {
		return chaperCount;
	}

	public void setChaperCount(int chaperCount) {
		this.chaperCount = chaperCount;
	}

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

	public int getSavedPosition() {
		return savedPosition;
	}

	public int getIsDownLoad() {
		return isDownLoad;
	}

	public void setIsDownLoad(int isDownLoad) {
		this.isDownLoad = isDownLoad;
	}

	public void setSavedPosition(int savedPosition) {
		this.savedPosition = savedPosition;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}

	public int getSentenceIndex() {
		return sentenceIndex;
	}

	public void setSentenceIndex(int sentenceIndex) {
		this.sentenceIndex = sentenceIndex;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public CollectorDto(String _id, String title, int savedPosition,
			int isDownLoad, Date date, int chaperCount, int countPage,
			int sentenceIndex, int position,String source) {
		super();
		this._id = _id;
		this.title = title;
		this.savedPosition = savedPosition;
		this.isDownLoad = isDownLoad;
		this.date = date;
		this.chaperCount = chaperCount;
		this.countPage = countPage;
		this.sentenceIndex = sentenceIndex;
		this.position = position;
		this.source = source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + chaperCount;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + isDownLoad;
		result = prime * result + savedPosition;
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
		CollectorDto other = (CollectorDto) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (chaperCount != other.chaperCount)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (isDownLoad != other.isDownLoad)
			return false;
		if (savedPosition != other.savedPosition)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
