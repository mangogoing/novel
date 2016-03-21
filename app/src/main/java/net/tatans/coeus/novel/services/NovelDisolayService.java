package net.tatans.coeus.novel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iflytek.cloud.ErrorCode;
import com.ushaqi.zhuishushenqi.util.CipherUtil;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.activities.IUpdateDisplayState;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.ChapterDto;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.dto.SummaryDto;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.JsonUtils;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.speaker.Speaker.onSpeechCompletionListener;
import net.tatans.coeus.util.Callback;
import net.tatans.coeus.util.Speaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载service
 * 
 * @author zzc
 * 
 */
@SuppressWarnings("deprecation")
public class NovelDisolayService extends Service {
	private static String TAG = "NovelDisolayService";
	private static int SHOW_WORD = 250;
	private String sResult;
	private TatansDb db;
	private List<CollectorDto> lt_collector; // 小说的collector list
	private onSpeechCompletionListener listener;
	private String[] split;
	private Map<String, Integer> map = new HashMap<String, Integer>();
	private Map<Integer, Integer> page = new HashMap<Integer, Integer>();
	private String NEXT_PAGE = "下一页", PRE_PAGE = "上一页";
	private int flag;// 用来标记是下一条还是下一页
	private String strContent; // 资讯内容 // 请求标题的id
	private Speaker AppSpeaker;
	private static net.tatans.coeus.speaker.Speaker speaker = null;

	// private ProgressDialog dialog;
	private String filePath;
	private boolean isComplete;
	private boolean isLoading;
	private static boolean isSpeaking;
	private String strShow = "";
	private RequestQueue mRequestQueue;
	private Intent intent;

	private boolean isCollector;
	private int currentPosition;// 该title在所有新闻数据里面的下标
	private int totalChapterCount;
	private int sentenceIndex = -1;// 句子的下标
	private int position;
	public static int sourceNum = 0;
	private int isDownLoad;
	private int countPage = 0;// 用于保存上一页的position
	private String bookId;
	private String chapterFilePath;
	static List<ChapterDto> ChapterList;

	private static IUpdateDisplayState iUpdateDisplayState = null;

	public static void setiUpdateDisplayState(
			IUpdateDisplayState iUpdateDisplayState) {
		NovelDisolayService.iUpdateDisplayState = iUpdateDisplayState;
	}

	public void setsResult(String sResult) {
		this.sResult = sResult;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("NovelDisolayService", "---->onStartCommand" + startId);
		if (intent != null) {
			this.intent = intent;
			switch (intent.getAction()) {
			case AppConstants.DISPLAY_STATE_PLAY_RECEIVER:
				initData();
				display();
				break;
			case AppConstants.DISPLAY_STATE_PAUSE_RECEIVER:
				pausePlayback();
				break;
			case AppConstants.DISPLAY_STATE_RESUME_RECEIVER:
				resumePlayback();
				break;
			case AppConstants.DISPLAY_STATE_RIGHT_GESTURE:
				gestureRight();
				break;
			case AppConstants.DISPLAY_STATE_LEFT_GESTURE:
				gestureLeft();
				break;
			case AppConstants.DISPLAY_STATE_PREIVOUS_CHAPTER_RECEIVER:
				preChapter();
				break;
			case AppConstants.DISPLAY_STATE_NEXT_CHAPTER_RECEIVER:
				nextChapter();
				break;
			case AppConstants.DISPLAY_STATE_PAUSE_NO_SPEAK:
				speakPause();
				break;
			case AppConstants.DISPLAY_STATE_STOP_RECEIVER:
				if (speaker != null) {
					stopSelf();
				}
				break;
			default:
				break;
			}
		}

		return START_STICKY;
	}

	private void initData() {
		db = TatansDb.create( AppConstants.TATANS_DB_NAME);
		mRequestQueue = Volley.newRequestQueue(NovelDisolayService.this);
		AppSpeaker = Speaker.getInstance(NovelDisolayService.this);
		speaker = net.tatans.coeus.speaker.Speaker
				.getInstance(NovelDisolayService.this);
		speaker.setSpeechOnResume(false);
		listener = new onSpeechCompletionListener() {
			@Override
			public void onCompletion(int arg0) {
				Log.e("TAG", "arg0=" + arg0);
				if (arg0 == ErrorCode.SUCCESS) {
					if (sentenceIndex >= split.length) {
						nextChapter();
					} else {
						readNextSentence();
					}
				}
			}
		};

		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); // 获取系统服务
		telManager.listen(new MobliePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
		isDownLoad = intent.getIntExtra("isDownLoad", -1);
		totalChapterCount = intent.getIntExtra("totalChapterCount", 0);
		bookId = intent.getStringExtra("bookId");
		currentPosition = intent.getIntExtra("currentPosition", 0);
		isCollector = intent.getBooleanExtra("isCollector", false);
		countPage = intent.getIntExtra("countPage", 0);
		sentenceIndex = intent.getIntExtra("sentenceIndex", -1);
		position = intent.getIntExtra("position", 0);

	}

	private void display() {
		chapterFilePath = Environment.getExternalStorageDirectory()
				+ "/tatans/novel/" + bookId + "/" + -1 + ".txt";
		if (isDownLoad == 1 && FileUtil.fileIsExists(chapterFilePath)
				|| isDownLoad == 0 && FileUtil.fileIsExists(chapterFilePath)) {
			String result;
			try {
				result = FileUtil.read(chapterFilePath).toString();
				ChapterList = JsonUtils.getChapterListByJson(result);
				totalChapterCount = ChapterList.size();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (FileUtil.fileIsExists(chapterFilePath) && isDownLoad == 3) {
			try {
				totalChapterCount = FileUtil.read2Chapter(chapterFilePath)
						.size();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		playPlayback();
	}

	private void playPlayback() {

		filePath = Environment.getExternalStorageDirectory() + "/tatans/novel/"
				+ bookId + "/" + currentPosition + ".txt";

		if (isDownLoad == 1 && FileUtil.fileIsExists(filePath)
				|| isDownLoad == 3 && FileUtil.fileIsExists(filePath)
				|| isDownLoad == 0 && FileUtil.fileIsExists(filePath)) {
			// 如果不为-1，并且该章节没有漏下则离线阅读
			new readFromSDcard().execute();
		} else {
			// 网络请求
			getSummaryResource(this, mRequestQueue, bookId, sourceNum);
		}
	}

	List<SummaryDto> summarylist;

	/**
	 * @param context
	 * @param bookId
	 * @return 网站资源列表
	 */
	private void getSummaryResource(Context context,
			final RequestQueue mRequestQueue, String bookId, final int sourceNum) {
		String bookBriefUrl = UrlUtil.RESOURCE_LIST + bookId;
		TatansHttp http = new TatansHttp();
		http.addHeader("User-Agent",
				"ZhuiShuShenQi/3.30.2(Android 5.1.1; TCL TCL P590L / TCL TCL P590L; )");
		http.get(bookBriefUrl, new HttpRequestCallBack<String>() {
			@Override
			public void onSuccess(String result) {
				super.onSuccess(result);
				summarylist = JsonUtils.getSummaryListByJson(result.toString());
				// totalChapterCount = summarylist.get(sourceNum)
				// .getChaptersCount();
				Log.d("OOOXXXOOO", summarylist.get(sourceNum).getName()
						+ "----" + sourceNum);
				getChapterResource(summarylist.get(sourceNum).get_id());
			}
		});
		// StringRequest jr = new StringRequest(Request.Method.GET,
		// UrlUtil.RESOURCE_LIST + bookId,
		// new Response.Listener<String>() {
		// @Override
		// public void onResponse(String response) {
		// List<SummaryDto> list = JsonUtils
		// .getSummaryListByJson(response.toString());
		// totalChapterCount = list.get(sourceNum)
		// .getChaptersCount();
		// Log.d("OOOXXXOOO", list.get(sourceNum).getName()
		// + "----" + sourceNum);
		// // for (int i = 0; i < list.size(); i++) {
		// getChapterResource(list.get(sourceNum).get_id());
		// // }
		//
		// }
		// }, new Response.ErrorListener() {
		// @Override
		// public void onErrorResponse(VolleyError error) {
		// }
		// });
		// mRequestQueue.add(jr);

	}

	/**
	 * @param
	 * @param newBookId
	 * @return 网站资源列表
	 */
	private void getChapterResource(final String newBookId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection cumtConnection;
				try {
					cumtConnection = (HttpURLConnection) new URL(
							UrlUtil.RESOURCE_BOOK_ID + newBookId
									+ "?view=chapters").openConnection();
					cumtConnection.setRequestProperty("User-Agent",
							"YouShaQi/2.23.2 (iPhone; iOS 9.2; Scale/2.00)");
					InputStream urlStream = cumtConnection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(urlStream, "UTF-8"));
					String line = "";
					String result = "";
					while ((line = reader.readLine()) != null) {
						result = result + line;
						Log.d("XXXXXXXXXX", line.toString());
					}
					reader.close();
					urlStream.close();
					ChapterList = JsonUtils.getChapterListByJson(result);

					getContentResource(ChapterList.get(currentPosition)
							.getLink());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();

	}

	static String url = "";

	private void getContentResource(final String link) {
		try {
			String url1 = "http://chapter2.zhuishushenqi.com";
			String url2 = "/chapter/" + URLEncoder.encode(link, "UTF8");
			String key = CipherUtil.getKey_t(url2);
			url = url1 + url2 + "?" + key;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection cumtConnection;
				try {
					cumtConnection = (HttpURLConnection) new URL(url)
							.openConnection();
					cumtConnection.setRequestProperty("User-Agent",
							"YouShaQi/2.23.2 (iPhone; iOS 9.2; Scale/2.00)");
					InputStream urlStream = cumtConnection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(urlStream, "UTF-8"));
					String line = "";
					String result = "";
					while ((line = reader.readLine()) != null) {
						result = result + line;
					}
					reader.close();
					urlStream.close();
					String str = JsonUtils.getNovelContent(result).replaceAll(
							" ", "");
					str = str.replace("\n", "");
					// if (str.contains("请安装最新版追书以便使用优质资源")) {
					// sourceNum++;
					// if (sourceNum < summarylist.size()) {
					//
					// getChapterResource(summarylist.get(sourceNum)
					// .get_id());
					// return;
					// } else {
					// str = "未获取到小说内容";
					// }
					// }
					totalChapterCount = summarylist.get(sourceNum)
							.getChaptersCount();
					strContent = (currentPosition + 1) + "."
							+ ChapterList.get(currentPosition).getTitle() + "："
							+ "\n正文：" + str;

					if (isCollector) {
						countPage = intent.getIntExtra("countPage", 0);
						sentenceIndex = intent.getIntExtra("sentenceIndex", -1);
						position = intent.getIntExtra("position", 0);
						isCollector = false;
					} else {
						countPage = 0;
						sentenceIndex = -1;
						position = 0;
					}
					if (!strContent.equals("")) {
						setContent(strContent);
					} else {
						if (iUpdateDisplayState != null) {
							iUpdateDisplayState.UpdateViewShow(currentPosition,
									"", totalChapterCount);
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					if (iUpdateDisplayState != null) {
						iUpdateDisplayState.UpdateViewShow(currentPosition,
								"未能获取到文本内容", totalChapterCount);
					}
				}
			}

		}).start();

	}

	// 从sd卡读取数据并解析显示
	class readFromSDcard extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				isCollector = true;
				String result = FileUtil.read(filePath).toString();
				json3Gson(result);

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!strContent.equals("")) {
				setContent(strContent);
			} else {
				showToast("小说内容出错，建议删除重新下载该资源");
				if (iUpdateDisplayState != null) {
					iUpdateDisplayState.UpdateViewShow(currentPosition,
							"小说内容出错，建议删除重新下载该资源", totalChapterCount);
				}
			}
		}

	}

	// 从sd卡读取数据并解析显示
	class readChapterFromSDcard extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				String result = FileUtil.read(chapterFilePath).toString();
				ChapterList = JsonUtils.getChapterListByJson(result);
				totalChapterCount = ChapterList.size();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!strContent.equals("")) {
				setContent(strContent);
			} else {
				showToast("小说内容出错，建议删除重新下载该资源");
				if (iUpdateDisplayState != null) {
					iUpdateDisplayState.UpdateViewShow(currentPosition,
							"小说内容出错，建议删除重新下载该资源", totalChapterCount);
				}
			}
		}

	}

	public void setContent(String str) {
		if (iUpdateDisplayState != null) {
			iUpdateDisplayState.UpdateDisplayState(true);

		}
		if (!str.endsWith("。")) {
			str = str + "。";
		}
		split = str.toString().split("，|。|！|？|；");
		for (int i = 0; i < split.length; i++) {
			map.put(split[i].toString(), i);
		}
		setsResult(str);
		readNextSentence();
	}

	/**
	 * 下一页
	 */
	public void nextPage() {
		position = position + SHOW_WORD;
		if (sResult.equals("") || sResult.equals(null)) {
			return;
		}
		if (position >= sResult.length()) {
			// position = sResult.length();
			Log.i(TAG, "position" + position);
			Log.i(TAG, "sResult" + sResult.length());
			Log.i(TAG, "没有下一页了");
			flag = 1;

		} else {
			flag = 2;
			loadPage(position);
			if (iUpdateDisplayState != null) {
				iUpdateDisplayState.UpdateViewShow(currentPosition, strShow,
						totalChapterCount);
			}
			countPage++;
			page.put(countPage, position);
		}
	}

	/**
	 * 从指定位置开始载入一页
	 */
	private void loadPage(int startPosition) {
		if (startPosition > sResult.length()) {
			nextChapter();
			return;
		}
		int endPosition = startPosition + SHOW_WORD;
		if (endPosition > sResult.length()) {
			endPosition = sResult.length();
		}
		Log.d("loadPage", "endPosition----->" + endPosition);
		Log.d("loadPage", "sResult.length()----->" + sResult.length());
		strShow = sResult.substring(startPosition, endPosition);

	}

	/**
	 * 上一页
	 */
	public void PrePage() {
		try {
			countPage--;
			if (countPage < 0) {
				countPage = 0;
				preChapter();
			} else {
				PRE_PAGE = "上一页";
				if (countPage == 0) {
					position = 0;
				} else {
					position = page.get(countPage);
				}
				loadPage(position);
				if (iUpdateDisplayState != null) {
					iUpdateDisplayState.UpdateViewShow(currentPosition,
							strShow, totalChapterCount);
				}
				// mEditText.resize();
				AppSpeaker.speech(PRE_PAGE, new Callback() {
					@Override
					public void onDone() {
						String[] nextSplit = strShow.split("，|。|！|？|；");
						Log.i(TAG, "nextSplit[1]:" + nextSplit[1].toString());
						sentenceIndex = map.get(nextSplit[1].toString());
					}
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读取下一句话
	public void readNextSentence() {
		if (sResult != null) {
			sentenceIndex++;
			if (speaker != null) {
				speaker.setOnSpeechCompletionListener(listener);
			}
			if (sentenceIndex >= split.length) {
				speaker.speech("本章已读完");
				isComplete = true;
			}
			if (sentenceIndex < split.length) {
				Log.i(TAG, "sResult：" + sResult);

				for (int i = sentenceIndex; i < split.length; i++) {
					if (split[sentenceIndex].equals("")
							|| split[sentenceIndex].length() < 0) {
						sentenceIndex++;
					}
				}
				loadPage(position);
				if (iUpdateDisplayState != null) {
					iUpdateDisplayState.UpdateViewShow(currentPosition,
							strShow, totalChapterCount);
				}
				speakPlay(split[sentenceIndex]);
				if (!strShow.contains(split[sentenceIndex])) {
					nextPage();
				}
			}

		} else {
			speaker.speech("本章内容为空");
		}
	}

	// 读取上一句话
	// public void readPreSentence() {
	//
	// sentenceIndex--;
	//
	// if (sentenceIndex < 0) {
	// sentenceIndex = -1;
	// readNextSentence();
	// } else if (sentenceIndex >= split.length) {
	// sentenceIndex = split.length - 1;
	// } else {
	// if (sentenceIndex >= split.length - 1) {
	// sentenceIndex = split.length - 2;
	// }
	//
	// if (sentenceIndex < 0) {
	// sentenceIndex = 0;
	// }
	// isSpeaking = true;
	// speaker.speech(split[sentenceIndex]);
	//
	// }
	// speaker.setOnSpeechCompletionListener(listener);
	// }

	Handler handler = new Handler();
	Runnable updateViewThread = new Runnable() {
		@Override
		public void run() {
			speakResume();
		}
	};

	/**
	 * 手指上滑
	 */
	public void pausePlayback() {
		// if (AppSpeaker != null) {
		if (isLoading) {
			showToast("内容加载中请稍后");
		} else {
			showToast("暂停播放");
			speakPause();
		}
		// }

	}

	/**
	 * 手指下滑
	 */
	public void resumePlayback() {
		// if (AppSpeaker != null) {
		showToast("继续播放");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				speakResume();
			}
		}, 1000);
		// AppSpeaker.speech("继续播放", new Callback() {
		// @Override
		// public void onDone() {
		// speakResume();
		// };
		// });
		// }
	}

	/**
	 * 手指左滑
	 */

	public void gestureLeft() {
		nextPage();
		if (flag == 2) {// 下一页
			speakPause();
			AppSpeaker.speech(NEXT_PAGE, new Callback() {
				@Override
				public void onDone() {
					handler.post(updateViewThread);
					String[] nextSplit = strShow.split("，|。|！|？|；");
					int size = nextSplit.length;
					Log.i(TAG, "nextSplit[1]:" + size);
					if (size > 1) {
						sentenceIndex = map.get(nextSplit[1].toString());
					} else {
						flag = 1;
					}

				};
			});
		}
		if (flag == 1) {// 下一章
			nextChapter();
		}

	}

	/**
	 * 手指右滑
	 */

	public void gestureRight() {
		PrePage();
		speakPause();

	}

	public void nextChapter() {
		speakPause();

		if (currentPosition >= (totalChapterCount - 1)) {
			currentPosition = totalChapterCount - 1;
			showToast("没有下一章了");
			if (flag == 1) {
				position = position - SHOW_WORD;
				flag = 3;
			}
			// AppSpeaker.speech("没有下一章了", new Callback() {
			//
			// @Override
			// public void onDone() {
			if (isComplete) {
				position = 0;
				if (iUpdateDisplayState != null) {
					iUpdateDisplayState.UpdateViewShow(currentPosition,
							"没有下一章了", totalChapterCount);
				}
			}
			// AppSpeaker.speech("全部章节播放完毕");
			// } else {
			// AppSpeaker.speech("暂停");
			// }
			// };
			// });
		} else {
			if (isDownLoad != -1) {
				dwonLoadNextChapter();
				return;
			} else {
				if (!isLoading) {
					currentPosition++;
					// new contentAsycTesk().execute();
					getContentResource(ChapterList.get(currentPosition)
							.getLink());
				} else {
					showToast("当前小说仍在加载中，请稍等");
				}
			}
		}
	}

	private void dwonLoadNextChapter() {
		speakPause();
		if (currentPosition < totalChapterCount - 1) {
			currentPosition++;
			playPlayback();
		}

	}

	public void preChapter() {
		speakPause();

		if (currentPosition <= 0) {
			currentPosition = 0;
			showToast("没有上一章了");
			// AppSpeaker.speech("没有上一章了", new Callback() {
			//
			// @Override
			// public void onDone() {
			// AppSpeaker.speech("暂停");
			// };
			// });
		} else {
			if (isDownLoad != -1) {
				dwonLoadPreChapter();
				return;
			} else {
				if (!isLoading) {
					currentPosition--;
					// new contentAsycTesk().execute();
					getContentResource(ChapterList.get(currentPosition)
							.getLink());
				} else {
					showToast("当前小说仍在加载中，请稍等");
				}
			}

		}

	}

	private void dwonLoadPreChapter() {
		if (currentPosition <= 0) {
			currentPosition = 0;
			speakPause();
			showToast("没有上一章了");
			// AppSpeaker.speech("没有上一章了", new Callback() {
			//
			// @Override
			// public void onDone() {
			// AppSpeaker.speech("暂停");
			// };
			// });

		} else {
			speakPause();
			currentPosition--;
			playPlayback();
		}
	}

	private String json3Gson(String result) {
		strContent = "";
		if (isDownLoad != 3) {
			String str = JsonUtils.getNovelContent(result).replaceAll(" ", "");
			str = str.replace("\n", "");
			strContent = (currentPosition + 1) + "."
					+ ChapterList.get(currentPosition).getTitle() + "："
					+ "\n正文：" + str;
		} else {
			String mResult = result.replace(" ", "");
			String mmResult = mResult.replace("\n", "");
			strContent = mmResult;
		}

		if (isCollector) {
			countPage = intent.getIntExtra("countPage", 0);
			sentenceIndex = intent.getIntExtra("sentenceIndex", -1);
			position = intent.getIntExtra("position", 0);
			isCollector = false;
		} else {
			countPage = 0;
			sentenceIndex = -1;
			position = 0;
		}

		return strContent;
	}

	/**
	 * 记录播放历史，若为收藏的小说，保存章节位置
	 * 
	 * @author shiyunfei
	 */
	private void save() {
		lt_collector = db.findAll(CollectorDto.class);
		if (lt_collector.size() == 0) {
			return;
		}
		for (int i = 0; i < lt_collector.size(); i++) {
			if (lt_collector.get(i).get_id().equals(bookId)) {
				CollectorDto item = db.findById(bookId, CollectorDto.class);
				sentenceIndex--;
				if (sentenceIndex <= -2) {
					sentenceIndex = -1;
				}
				if (currentPosition < 0) {
					currentPosition = 0;
				}
				CollectorDto collector = new CollectorDto(bookId,
						item.getTitle(), currentPosition, item.getIsDownLoad(),
						new Date(), totalChapterCount, countPage,
						sentenceIndex, position, "");
				db.update(collector);
				return;
			}
		}
	}

	/**
	 * 
	 * @author zzc 电话监听器类
	 */
	private class MobliePhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 挂机状态
				if (speaker != null && isSpeaking) {
					speaker.resume();
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 通话状态

			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				if (speaker != null && isSpeaking) {
					speaker.pause();
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		speakStop();
		save();

	}

	private void speakPlay(String text) {
		if (speaker != null) {
			isSpeaking = true;
			speaker.speech(text);
		}
	}

	private void speakPause() {
		if (speaker != null) {
			isSpeaking = false;
			speaker.pause();
			if (iUpdateDisplayState != null) {
				iUpdateDisplayState.UpdateDisplayState(isSpeaking);
			}
		}
	}

	private void speakResume() {
		if (speaker != null) {
			isSpeaking = true;
			speaker.resume();
			if (iUpdateDisplayState != null) {
				iUpdateDisplayState.UpdateDisplayState(isSpeaking);
			}
		}
	}

	private void speakStop() {
		if (AppSpeaker != null) {
			AppSpeaker.stop();
			AppSpeaker = null;
		}
		if (speaker != null) {
			isSpeaking = false;
			speaker.stop();
			speaker.setOnSpeechCompletionListener(null);
			speaker = null;
		}
	}

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}

}
