package net.tatans.coeus.novel.tools;

public class UrlUtil {
	/** 搜索------ */
	/* 自動提示 */
	public static final String AUTO_COMPLETE = "http://api.zhuishushenqi.com/book/auto-complete?query=";
	/* 模糊搜索 */
	public static final String FUZZY_SEARCH = "http://api.zhuishushenqi.com/book/fuzzy-search?query=";

	/** 排行榜------ */
	/* 首页排行榜（不带参数）--子排行榜（带参数） */
	public static final String RANK = "http://api.zhuishushenqi.com/ranking/";
	/* 进入书籍（有简介） */
	public static final String BOOK = "http://api.zhuishushenqi.com/book/";
	/* 进入书籍（有简介） */
	public static final String BY_BOOK = "http://api.zhuishushenqi.com/aggregation-source/by-book?book=";
	/* 章节列表 */
	public static final String CHAPTER_LIST_URL = "http://api.zhuishushenqi.com/mix-toc/";
	/* 章节列表 */
	public static final String CHAPTER_LIST_URL_EASOU = "http://api.easou.com/api/bookapp/chapter_list.m?";
	/* 章节列表 */
	public static final String CHAPTER_LIST_URL_LEIDIAN = "http://m.leidian.com/ebook/detail/index.php?c=ebook&a=chapterlist&bid=";

	/* 章节内容 */
	public static final String CHAPTER_CONTENT = "http://chapter.zhuishushenqi.com/chapter/";

	public static final String CHAPTER_CONTENT_EASOU = "http://api.easou.com/api/bookapp/chapter.m?";

	public static final String CHAPTER_CONTENT_LEIDIAN = "http://m.leidian.com/index.php?c=ebook&a=chapterData&fmt=json&bid=";

	/**
	 * 主题----- 本周最日、最近发布、最多收藏 一次请求20条 start为第一条开始 0 ，请求下20条传start=20
	 * 再下20条为start=40
	 */
	/* 一周最热 */
	public static final String HOT_WEEKLY = "http://api.zhuishushenqi.com/book-list?duration=last-seven-days&sort=collectorCount&start=";
	/* 最近发布 */
	public static final String RECENT_PUBLISH = "http://api.zhuishushenqi.com/book-list?duration=all&sort=created&start=";
	/* 最多收藏 */
	public static final String MAX_COLLECTOR = "http://api.zhuishushenqi.com/book-list?duration=all&sort=collectorCount&start=";
	/* 主题下一层，书单 */
	public static final String BOOK_LIST = "http://api.zhuishushenqi.com/book-list/";

	/** 小说分类-----start是第几个开始 limit是多少个 */

	/* 玄幻、网游、爱情、都市等 */
	public static final String CATS = "http://api.zhuishushenqi.com/cats";

	/* 进入某个分类 tag为类别名称，例如tag=玄幻 */
	public static final String CAT_LIST = "http://api.zhuishushenqi.com/book/by-tag?";

	public static final String RESOURCE_LIST = "http://api.zhuishushenqi.com/toc?view=summary&book=";
	
	public static final String VIEW_CHAPTERS = "http://api.zhuishushenqi.com/toc/";


	public static final String ACTION = "net.tatans.coeus.novel.DownLoadService";
	public static final String FINISH_ACTION = "net.tatans.coeus.novel.DownLoadService.finish";

	/** 下载时章节列表保存文件.TXT位置 */
	public static final int CHAPTERLIST_TXT = -1;
	/** 下载时章节列表保存文件.TXT位置 */
	public static final int SOURCE_LIST_TXT = -2;

	public static final String RESOURCE_BOOK_ID = "http://api.zhuishushenqi.com/toc/";

}
