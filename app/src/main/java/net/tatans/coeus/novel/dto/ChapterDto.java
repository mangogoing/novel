package net.tatans.coeus.novel.dto;

import net.tatans.coeus.annotation.sqlite.Table;

/** 我的收藏 */
@Table(name = "MyCollector")
public class ChapterDto {
	private String title;
	private String link;
	private String chapterName;
	private int sort;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
	
	

}
