package net.tatans.coeus.novel.broadCastRecivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansToast;

public class LoadFileReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean succeed = intent.getBooleanExtra("succeed", false);
        String novelName = intent.getStringExtra("novelName");
        if (succeed) {
            TatansToast.showAndCancel(novelName + "，导入完成");
        } else {
            TatansToast.showAndCancel(novelName + "，导入异常");
        }

    }
//	private TatansDb db;
//	private List<CollectorDto> list = new ArrayList<CollectorDto>();
//	public int sentenceIndex = -1;// 句子的下标
//	private int isDownLoad;
//	private int position;
//	public List<CollectorDto> lt_collector; // 小说的collector list
//	// // 手势监测
//	private static int countPage = 0;// 用于保存上一页的position
//	private static int currentPosition;// 该title在所有新闻数据里面的下标
//	// private ProgressDialog dialog;
//	private String title;
//	private String sourceId;
//	private int totalChapterCount;
//	private String source;
//
//	@Override
//	public void onReceive(final Context context, Intent intent) {
//		// TatansToast.showAndCancel(context, intent.getAction());
//		if (intent.getAction().equals(AppConstants.DISPLAY_STATE_NOVEL_RECEIVE)) {
//			switch (intent.getStringExtra("displayState")) {
//
//			case AppConstants.DISPLAY_STATE_PLAY_RECEIVER:
//				if (NovelDisolayService.speaker == null) {
//					db = TatansDb.create(context, AppConstants.TATANS_DB_NAME);
//					list = db.findAll(CollectorDto.class, " date desc");
//					if (list.isEmpty()) {
//						TatansToast.showAndCancel(context,
//								"没有收藏的小说，请到小说阅读应用中添加小说到书架");
//					} else {
//						isDownLoad = list.get(0).getIsDownLoad();
//						totalChapterCount = list.get(0).getChaperCount();
//						sourceId = list.get(0).get_id();
//						source = list.get(0).getSource();
//						currentPosition = list.get(0).getSavedPosition();
//						title = list.get(0).getTitle();
//						countPage = list.get(0).getCountPage();
//						sentenceIndex = list.get(0).getSentenceIndex();
//						position = list.get(0).getPosition();
//						startNovelDisolayService(context,
//								AppConstants.DISPLAY_STATE_PLAY_RECEIVER);
//					}
//				} else {
//					startNovelDisolayService(context,
//							AppConstants.DISPLAY_STATE_RESUME_RECEIVER);
//				}
//
//				break;
//			case AppConstants.DISPLAY_STATE_PAUSE_RECEIVER:
//				startNovelDisolayService(context,
//						AppConstants.DISPLAY_STATE_PAUSE_RECEIVER);
//				break;
//			case AppConstants.DISPLAY_STATE_PREIVOUS_CHAPTER_RECEIVER:
//				startNovelDisolayService(context,
//						AppConstants.DISPLAY_STATE_PREIVOUS_CHAPTER_RECEIVER);
//				break;
//			case AppConstants.DISPLAY_STATE_NEXT_CHAPTER_RECEIVER:
//				startNovelDisolayService(context,
//						AppConstants.DISPLAY_STATE_NEXT_CHAPTER_RECEIVER);
//				break;
//			case AppConstants.DISPLAY_STATE_STOP_RECEIVER:
//				startNovelDisolayService(context,
//						AppConstants.DISPLAY_STATE_STOP_RECEIVER);
//				break;
//
//			default:
//				break;
//			}
//		}
//
//	}
//
//	private void startNovelDisolayService(Context context, String action) {
//		Intent intent = new Intent(context, NovelDisolayService.class);
//		intent.setAction(action);
//		if (action.equals(AppConstants.DISPLAY_STATE_PLAY_RECEIVER)) {
//			intent.putExtra("isDownLoad", isDownLoad);
//			intent.putExtra("totalChapterCount", totalChapterCount);
//			intent.putExtra("sourceId", sourceId);
//			intent.putExtra("source", source);
//			intent.putExtra("currentPosition", currentPosition);
//			intent.putExtra("isCollector", true);
//			intent.putExtra("title", title);
//			intent.putExtra("countPage", countPage);
//			intent.putExtra("sentenceIndex", sentenceIndex);
//			intent.putExtra("position", position);
//		}
//		context.startService(intent);
//	}

}
