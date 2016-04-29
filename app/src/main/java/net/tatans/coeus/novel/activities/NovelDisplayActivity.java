package net.tatans.coeus.novel.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.services.NovelDisolayService;
import net.tatans.coeus.novel.tools.HomeWatcher;
import net.tatans.coeus.novel.tools.HomeWatcher.OnHomePressedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * Project Name:hyperion-baselib
 * Package:net.tatans.hyperion.baselib
 * FileName:SentenceSplitActivity.java
 * Purpose:长文本播放界面
 * Create Time: 2014-9-29 下午3:53:14
 * Create Specification:该类为长文本播放界面
 * 		继承该类后，将要朗读的文本用setContent(“要播放的内容”);方法
 * 		即可朗读文本
 * 		该界面有暂停、继续播放、获取当前读到第几句
 * 	
 * Version: 1.0
 * </pre>
 * 
 * @author zhouzhaocai
 */

@SuppressLint("HandlerLeak")
//public class NovelDisplayActivity extends Activity implements OnClickListener,
//		IUpdateDisplayState {
public class NovelDisplayActivity extends Activity {
//	private static String TAG = "ContentSplitActivity";
//	private TextView mEditText;
//	LinearLayout container;
//	private TextView tv_loading, tv_next_chapter, tv_pre_chapter,
//			tv_pause_or_play, tv_replace_resources, tv_more;
//	public int sentenceIndex = -1;// 句子的下标
//	private int isDownLoad;
//	private int position;
//	public TatansDb db;
//	public List<CollectorDto> lt_collector; // 小说的collector list
//	private GestureDetector mDetector; // // 手势监测
//	private static int countPage = 0;// 用于保存上一页的position
//	private Speaker AppSpeaker;
//	private static int currentPosition;// 该title在所有新闻数据里面的下标
//	private String title;
//	private boolean isSpeak;
//	private boolean isCollector;
//	private int requestCode = 1;
//	private HomeWatcher mHomeWatcher = null;
//	private String bookId;
//	private int totalChapterCount;
//	private boolean mIsSpeaking;
//	private String strNovel;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.display_activity);
//		NovelDisolayService.setiUpdateDisplayState(this);
//		setTitle("");
//		initView();
//		initData();
//		startNovelDisolayService(this, AppConstants.DISPLAY_STATE_PLAY_RECEIVER);
//
//	}
//
//	/**
//	 * 初始化view
//	 */
//	private void initView() {
//		container = (LinearLayout) findViewById(R.id.container);
//		tv_loading = (TextView) findViewById(R.id.tv_loading);
//		tv_replace_resources = (TextView) findViewById(R.id.tv_replace_resources);
//		tv_replace_resources.setOnClickListener(this);
//		tv_next_chapter = (TextView) findViewById(R.id.tv_next_chapter);
//		tv_next_chapter.setOnClickListener(this);
//		tv_pause_or_play = (TextView) findViewById(R.id.tv_pause_or_play);
//		tv_pause_or_play.setOnClickListener(this);
//		tv_pre_chapter = (TextView) findViewById(R.id.tv_pre_chapter);
//		tv_pre_chapter.setOnClickListener(this);
//		tv_more = (TextView) findViewById(R.id.tv_more);
//		tv_more.setOnClickListener(this);
//		mEditText = (TextView) findViewById(R.id.tv_body);
//		// mEditText.setCursorVisible(false); // 设置输入框中的光标不可见
//		mEditText.setText("内容加载中，请稍后");
//		mEditText.clearFocus();
//
//	}
//
//	private void initData() {
//		AppSpeaker = Speaker.getInstance(getApplicationContext());
//		db = TatansDb.create( AppConstants.TATANS_DB_NAME);
//		mDetector = new GestureDetector(NovelDisplayActivity.this,
//				new myOnGestureListener());
//		mHomeWatcher = new HomeWatcher(NovelDisplayActivity.this);
//		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
//
//			@Override
//			public void onHomePressed() {
//				startNovelDisolayService(NovelDisplayActivity.this,
//						AppConstants.DISPLAY_STATE_PAUSE_NO_SPEAK);
//			}
//
//			@Override
//			public void onHomeLongPressed() {
//			}
//		});
//
//		Intent intent = getIntent();
//		isDownLoad = intent.getIntExtra("isDownLoad", -1);
//		bookId = intent.getStringExtra("bookId");
//		currentPosition = intent.getIntExtra("currentPosition", 0);
//		title = intent.getStringExtra("title");
//		isCollector = intent.getBooleanExtra("isCollector", false);
//		countPage = intent.getIntExtra("countPage", 0);
//		sentenceIndex = intent.getIntExtra("sentenceIndex", -1);
//		position = intent.getIntExtra("position", 0);
//
//	}
//
//	private void startNovelDisolayService(Context context, String action) {
//		Intent intent = new Intent(context, NovelDisolayService.class);
//		intent.setAction(action);
//		if (action.equals(AppConstants.DISPLAY_STATE_PLAY_RECEIVER)) {
//			intent.putExtra("isDownLoad", isDownLoad);
//			intent.putExtra("bookId", bookId);
//			intent.putExtra("currentPosition", currentPosition);
//			intent.putExtra("isCollector", isCollector);
//			intent.putExtra("title", title);
//			intent.putExtra("countPage", countPage);
//			intent.putExtra("sentenceIndex", sentenceIndex);
//			intent.putExtra("position", position);
//		}
//		context.startService(intent);
//	}
//
//	private class myOnGestureListener extends
//			GestureDetector.SimpleOnGestureListener {
//
//		@Override
//		public void onLongPress(MotionEvent e) {
//			super.onLongPress(e);
//			Log.e(TAG, "长按");
//		}
//
//		/**
//		 * 手势操作方法 向上:暂停 向下:继续
//		 *
//		 *
//		 */
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//				float velocityY) {
//			// e1为向量的起点，e2为向量的终点 , velocityX在X轴上面的速度 xx像素/s, velocityY在Y轴上面的滑动速度
//			System.out.println("mDetector onFling()");
//			float xlength = e1.getX() - e2.getX();
//			float ylength = e1.getY() - e2.getY();
//
//			// 向上向下
//			if (ylength > 120 && Math.abs(xlength / ylength) < 120) {
//				Log.e("SentenceSplitActivity", "mDetector 向上:");
//				// startNovelDisolayService(NovelDisplayActivity.this,
//				// AppConstants.DISPLAY_STATE_PAUSE_RECEIVER);
//			}
//			if (ylength < -120 && Math.abs(xlength / ylength) < 1) {
//				Log.e("SentenceSplitActivity", "mDetector 向下:");
//				if (mIsSpeaking) {
//					startNovelDisolayService(NovelDisplayActivity.this,
//							AppConstants.DISPLAY_STATE_PAUSE_RECEIVER);
//				} else {
//					startNovelDisolayService(NovelDisplayActivity.this,
//							AppConstants.DISPLAY_STATE_RESUME_RECEIVER);
//				}
//			}
//			if (xlength > 120 && Math.abs(ylength / xlength) < 1) {
//				Log.e("SentenceSplitActivity", "mDetector 向左:");
//				startNovelDisolayService(NovelDisplayActivity.this,
//						AppConstants.DISPLAY_STATE_LEFT_GESTURE);
//
//			}
//			if (xlength < -120 && Math.abs(ylength / xlength) < 1) {
//				Log.e("SentenceSplitActivity", "mDetector 向右:");
//				startNovelDisolayService(NovelDisplayActivity.this,
//						AppConstants.DISPLAY_STATE_RIGHT_GESTURE);
//			}
//			return false;
//		}
//
//	}
//
//	/**
//	 * 手势按钮分发
//	 */
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		if (mDetector != null) {
//			if (mDetector.onTouchEvent(ev))
//				return true;
//		}
//		return super.dispatchTouchEvent(ev);
//	}
//
//	// 打断talkback
//	public void interruptTalkback(Context context) {
//		AccessibilityManager accessibilityManager = (AccessibilityManager) context
//				.getSystemService(Context.ACCESSIBILITY_SERVICE);
//		if (accessibilityManager.isEnabled()) {
//			accessibilityManager.interrupt();
//			Log.e("hhh", "打断talkback");
//		} else {
//			Log.e("hhh", "不打断打断talkback");
//		}
//
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			startNovelDisolayService(this,
//					AppConstants.DISPLAY_STATE_STOP_RECEIVER);
//			if (hasCollectored() == false) {
//				dialog();
//			} else {
//				this.finish();
//			}
//		}
//		return false;
//	}
//
//	private void dialog() {
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//				NovelDisplayActivity.this);
//		alertDialogBuilder.setMessage("您还未收藏该小说，是否收藏");
//		alertDialogBuilder.setTitle("提示");
//		alertDialogBuilder.setPositiveButton("收藏。",
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(final DialogInterface dialog, int which) {
//						CollectorDto collector = new CollectorDto(bookId,
//								title, currentPosition, -1, new Date(),
//								totalChapterCount, countPage, sentenceIndex,
//								position, "");
//						db.save(collector);
//						AppSpeaker.speech("收藏成功", new Callback() {
//							@Override
//							public void onDone() {
//								super.onDone();
//								dialog.dismiss();
//								NovelDisplayActivity.this.finish();
//							}
//						});
//
//					}
//				});
//		alertDialogBuilder.setNegativeButton("取消。",
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						NovelDisplayActivity.this.finish();
//					}
//				});
//		alertDialogBuilder.create().show();
//	}
//
//	// 判断是否收藏过
//	private boolean hasCollectored() {
//		lt_collector = db.findAll(CollectorDto.class);
//		if (lt_collector.size() == 0) {
//			return false;
//		}
//		List<String> list_id = new ArrayList<String>();
//		for (int i = 0; i < lt_collector.size(); i++) {
//			list_id.add(lt_collector.get(i).get_id());
//		}
//		if (list_id.contains(bookId)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == 1) {
//			if (resultCode == RESULT_OK) {
//				tv_loading.setVisibility(View.VISIBLE);
//				countPage = 0;
//				sentenceIndex = -1;
//				position = 0;
//				isDownLoad = data.getIntExtra("isDownLoad", -1);
//				currentPosition = data.getIntExtra("currentPosition", 0);
//				title = data.getStringExtra("title");
//				totalChapterCount = data.getIntExtra("totalChapterCount", 0);
//				bookId = data.getStringExtra("bookId");
//				startNovelDisolayService(NovelDisplayActivity.this,
//						AppConstants.DISPLAY_STATE_PLAY_RECEIVER);
//			}
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.tv_more:
//			startNovelDisolayService(NovelDisplayActivity.this,
//					AppConstants.DISPLAY_STATE_PAUSE_NO_SPEAK);
//			Intent intentMore = new Intent();
//			intentMore.setClass(NovelDisplayActivity.this, MoreActivity.class);
//			intentMore.putExtra("isDownLoad", isDownLoad);
//			intentMore.putExtra("title", title);
//			intentMore.putExtra("currentPosition", currentPosition);
//			intentMore.putExtra("bookId", bookId);
//			intentMore.putExtra("totalChapterCount", totalChapterCount);
//			startActivityForResult(intentMore, requestCode);
//			break;
//		case R.id.tv_pre_chapter:
//			startNovelDisolayService(NovelDisplayActivity.this,
//					AppConstants.DISPLAY_STATE_PREIVOUS_CHAPTER_RECEIVER);
//			break;
//		case R.id.tv_pause_or_play:
//			if (mIsSpeaking) {
//				startNovelDisolayService(NovelDisplayActivity.this,
//						AppConstants.DISPLAY_STATE_PAUSE_RECEIVER);
//			} else {
//				startNovelDisolayService(NovelDisplayActivity.this,
//						AppConstants.DISPLAY_STATE_RESUME_RECEIVER);
//			}
//			break;
//		case R.id.tv_next_chapter:
//			startNovelDisolayService(this,
//					AppConstants.DISPLAY_STATE_NEXT_CHAPTER_RECEIVER);
//			break;
//
//		default:
//			break;
//		}
//
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		if (mHomeWatcher != null) {
//			mHomeWatcher.stopWatch();
//		}
//
//	}
//
//	protected void onResume() {
//		super.onResume();
//		if (mHomeWatcher != null) {
//			mHomeWatcher.startWatch();
//		}
//		if (isSpeak) {
//			setTitle("播放界面");
//		} else {
//			isSpeak = true;
//		}
//
//	};
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
//
//	Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (msg.what == AppConstants.IS_NULL_NOVEL) {
//				TatansToast.showAndCancel( "小说内容为空");
//				finish();
//			} else if (msg.what == AppConstants.IS_NO_NEXT_CHAPTER) {
//				TatansToast.showAndCancel( "没有下一章了");
//				finish();
//			} else if (msg.what == AppConstants.IS_SUCCEED) {
//				tv_loading.setVisibility(View.GONE);
//				mEditText.setText(strNovel);
//			} else if (msg.what == AppConstants.IS_PAUSE_OR_RESUME) {
//				if (mIsSpeaking) {
//					tv_pause_or_play.setText("暂停");
//				} else {
//					tv_pause_or_play.setText("播放");
//				}
//			}
//
//		}
//	};
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		TatansToast.cancel();
//	}
//
//	@Override
//	public void UpdateDisplayState(boolean isSpeaking) {
//		mIsSpeaking = isSpeaking;
//		handler.sendEmptyMessage(AppConstants.IS_PAUSE_OR_RESUME);
//
//	}
//
//	@Override
//	public void UpdateViewShow(int position, String strShow, int chapterCount) {
//		currentPosition = position;
//		totalChapterCount = chapterCount;
//		if (strShow.equals("")) {
//			handler.sendEmptyMessage(AppConstants.IS_NULL_NOVEL);
//			return;
//		} else if (strShow.equals("没有下一章了")) {
//			handler.sendEmptyMessage(AppConstants.IS_NO_NEXT_CHAPTER);
//			return;
//		} else {
//			strNovel = strShow;
//			handler.sendEmptyMessage(AppConstants.IS_SUCCEED);
//			return;
//		}
//
//	}

}
