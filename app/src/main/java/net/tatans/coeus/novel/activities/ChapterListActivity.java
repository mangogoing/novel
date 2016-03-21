package net.tatans.coeus.novel.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.TitleAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.ChapterDto;
import net.tatans.coeus.novel.dto.SummaryDto;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.JsonUtils;
import net.tatans.coeus.novel.tools.SharedPreferencesUtil;
import net.tatans.coeus.novel.tools.UrlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChapterListActivity extends BaseActivity implements
		OnItemClickListener {

	private ListView lv_one_list;
	private TextView tv_loading;
	private TitleAdapter listAdapter;
	private String title, chapterFilePath;
	private int pageCount, isDownLoad;
	private int currentPage = 1;
	private List<String> titleList = new ArrayList<String>();
	private List<Integer> sortList = new ArrayList<Integer>();
	Handler handler = new Handler();
	private int currentPosition;
	private boolean isBookBriefActivity;
	private boolean isSpeak;
	private TatansDb db;
	private String bookId;
	private int totalChapterCount;
	protected List<ChapterDto> ChapterList;
	private RequestQueue mRequestQueue;
	private int sourceNum = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = TatansDb.create( "MyCollector");
		setContentView(R.layout.list);
		lv_one_list = (ListView) findViewById(R.id.lv_main);
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		mRequestQueue = Volley.newRequestQueue(ChapterListActivity.this);
		Intent intent = getIntent();
		totalChapterCount = intent.getIntExtra("totalChapterCount", 0);
		bookId = intent.getStringExtra("bookId");
		isDownLoad = intent.getIntExtra("isDownLoad", -1);
		currentPosition = intent.getIntExtra("currentPosition", 0);
		isBookBriefActivity = intent
				.getBooleanExtra("BookBriefActivity", false);
		currentPage = (int) Math.ceil((currentPosition + 1)
				/ AppConstants.APP_PAGE_SIZE);
		title = intent.getStringExtra("title");
		setTitle(title + "章节列表");
		sourceNum = SharedPreferencesUtil.readData(this);
		init();
		lv_one_list.setOnItemClickListener(this);

	}

	private void init() {
		chapterFilePath = Environment.getExternalStorageDirectory()
				+ "/tatans/novel/" + bookId + "/" + UrlUtil.CHAPTERLIST_TXT
				+ ".txt";
		if (isDownLoad == 3) {

			json2Gson("");
		} else if (isDownLoad == 1) {
			// 如果章节列表txt存在读取章节列表
			new readFromSDcard().execute();

		} else {
			// 网络请求
			getSummaryResource(this, mRequestQueue, bookId);
		}

	}

	class readFromSDcard extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				String result = FileUtil.read(chapterFilePath).toString();
				json2Gson(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * @param context
	 * @param bookId
	 * @return 网站资源列表
	 */
	private void getSummaryResource(Context context,
			final RequestQueue mRequestQueue, String bookId) {
		StringRequest jr = new StringRequest(Request.Method.GET,
				UrlUtil.RESOURCE_LIST + bookId,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						List<SummaryDto> summarylist = JsonUtils
								.getSummaryListByJson(response.toString());
						if (summarylist.size() > 0) {
							if (sourceNum > summarylist.size() - 1) {
								sourceNum = summarylist.size() - 1;
							}
							totalChapterCount = summarylist.get(sourceNum)
									.getChaptersCount();
							Log.d("OOOXXXOOO", summarylist.get(sourceNum)
									.getName() + "----" + sourceNum);
							getChapterResource(summarylist.get(sourceNum)
									.get_id());
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});
		mRequestQueue.add(jr);

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
					titleList = JsonUtils.getChapterNameListByJson(result);
					for (int i = 0; i < titleList.size(); i++) {
						sortList.add(i + 1);
					}
					pageCount = (int) Math.ceil(totalChapterCount
							/ AppConstants.APP_PAGE_SIZE);
					handler.post(result2json);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();

	}

	private void json2Gson(String result) {
		Log.d("ClassificatTwoActivity", result + "");
		titleList.clear();
		sortList.clear();
		if (isDownLoad == 3) {
			try {
				titleList = FileUtil.read2Chapter(chapterFilePath);
				totalChapterCount = titleList.size();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < totalChapterCount; i++) {
				sortList.add(i + 1);
			}
			// 总页数
			pageCount = (int) Math.ceil(totalChapterCount
					/ AppConstants.APP_PAGE_SIZE);

			handler.post(result2json);
		} else {
			ChapterList = JsonUtils.getChapterListByJson(result);
			totalChapterCount = ChapterList.size();
			for (int i = 0; i < ChapterList.size(); i++) {
				titleList.add(ChapterList.get(i).getTitle());
				sortList.add(i + 1);
			}
			// 总页数
			pageCount = (int) Math.ceil(totalChapterCount
					/ AppConstants.APP_PAGE_SIZE);

			handler.post(result2json);
		}

	}

	Runnable result2json = new Runnable() {

		@Override
		public void run() {
			// try {
			// Thread.sleep(800);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
			setListData();
		}
	};

	public void setListData() {
		if (titleList.size() == 0) {
			return;
		}
		int to = (int) (AppConstants.APP_PAGE_SIZE + AppConstants.APP_PAGE_SIZE
				* (currentPage - 1));
		int from = (int) (AppConstants.APP_PAGE_SIZE * (currentPage - 1));
		if (to > titleList.size()) {
			to = titleList.size();
		}

		// listAdapter = new ArrayAdapter<String>(getApplication(),
		// R.layout.list_item, R.id.tv_item_name, titleList);
		listAdapter = new TitleAdapter(getApplicationContext(), titleList);
		lv_one_list.setAdapter(listAdapter);
		lv_one_list.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// int currentPosition = (currentPage - 1)
		// * (int) AppConstants.APP_PAGE_SIZE + position;

		Intent intent = new Intent();
		intent.putExtra("bookId", bookId);
		intent.putExtra("totalChapterCount", totalChapterCount);
		intent.putExtra("isDownLoad", isDownLoad);
		intent.putExtra("currentPosition", sortList.get(position) - 1);
		intent.putExtra("title", title);
		if (isBookBriefActivity) {
			intent.setClass(ChapterListActivity.this,
					NovelDisplayActivity.class);
			startActivity(intent);
		} else {
			intent.setClass(ChapterListActivity.this, MoreActivity.class);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/*
	 * @Override public void up() { if (pageCount > 1) {
	 * TatansToast.showAndCancel(this, "双指左右滑动可翻页"); } }
	 * 
	 * @Override public void left() { currentPage++; if (currentPage >
	 * pageCount) { currentPage = pageCount; showToast("没有下一页了"); } else {
	 * showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
	 * setListData();
	 * 
	 * }
	 * 
	 * }
	 * 
	 * @Override public void right() { currentPage--; if (currentPage < 1) {
	 * currentPage = 1; showToast("没有上一页了");
	 * 
	 * } else { showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
	 * setListData();
	 * 
	 * } }
	 * 
	 * @Override public void down() { if (pageCount > 1) {
	 * TatansToast.showAndCancel(this, "双指左右滑动可翻页"); } }
	 */

	// private void speechShow(String text) {
	// if (speaker == null) {
	// speaker = Speaker.getInstance(ChapterListActivity.this);
	// }
	// speaker.speech(text);
	// }

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isSpeak) {
			setTitle(title + "章节列表");
		} else {
			isSpeak = true;
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		TatansToast.cancel();
	}

	@Override
	public void left() {
		// TODO Auto-generated method stub

	}

	@Override
	public void right() {
		// TODO Auto-generated method stub

	}

	@Override
	public void up() {
		// TODO Auto-generated method stub

	}

	@Override
	public void down() {
		// TODO Auto-generated method stub

	}
}
