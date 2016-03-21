package net.tatans.coeus.novel.constant;

public class AppConstants {

	/** 每页显示个数 */
	public static final float APP_PAGE_SIZE = 10;

	public static final String TATANS_DB_NAME = "MyCollector";

	public static final String FAILED_TO_REQUEST_DATA = "未能请求到数据，请检查网络";

	public static final String DISPLAY_STATE_NOVEL_RECEIVE = "net.tatans.rhea.launcher.novel";

	public static final String DISPLAY_STATE_PREIVOUS_CHAPTER_RECEIVER = "net.tatans.rhea.launcher.novel.previous";
	public static final String DISPLAY_STATE_NEXT_CHAPTER_RECEIVER = "net.tatans.rhea.launcher.novel.next";
	public static final String DISPLAY_STATE_PLAY_RECEIVER = "net.tatans.rhea.launcher.novel.play";
	public static final String DISPLAY_STATE_PAUSE_RECEIVER = "net.tatans.rhea.launcher.novel.pause";
	public static final String DISPLAY_STATE_RESUME_RECEIVER = "net.tatans.rhea.launcher.novel.resume";
	public static final String DISPLAY_STATE_STOP_RECEIVER = "net.tatans.rhea.launcher.novel.stop";
	public static final String DISPLAY_STATE_LEFT_GESTURE = "net.tatans.rhea.launcher.novel.left";
	public static final String DISPLAY_STATE_RIGHT_GESTURE = "net.tatans.rhea.launcher.novel.right";
	public static final String DISPLAY_STATE_PAUSE_NO_SPEAK = "net.tatans.rhea.launcher.novel.pause_no_speak";

	public static final int IS_PAUSE_OR_RESUME = 1;
	public static final int IS_SUCCEED = 2;
	public static final int IS_NULL_NOVEL = 3;
	public static final int IS_NO_NEXT_CHAPTER = 4;

}
