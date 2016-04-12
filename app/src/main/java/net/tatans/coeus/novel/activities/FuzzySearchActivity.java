package net.tatans.coeus.novel.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.BookListAdapter;
import net.tatans.coeus.novel.adapter.TitleAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.BookListDto;
import net.tatans.coeus.novel.tools.InputMethodlUtil;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.speaker.Speaker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FuzzySearchActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener, TextWatcher {
	private List<String> searchList = new ArrayList<String>();
	private ListView lv_search_list;
	private ListView lv_tips_list;
	private TitleAdapter listAdapter;
	private BookListAdapter bookListAdapter;
//	private ArrayAdapter<String> emptyAdapter;
	private int pageCount;
	private int currentPage = 1;
	private List<BookListDto> ClassificatList = new ArrayList<BookListDto>();
	private Handler handler = new Handler();
	private Handler handlerSpeak = new Handler();
	private Speaker speaker;
	private EditText av_autotext;
	private TextView tv_search;
	private TextView tv_loading;
	private boolean isSearch;// 判断是否为搜索
	private ArrayAdapter<String> arrayAdapter;
	private List<String> array = new ArrayList<String>();
	private boolean isLoading;
	private String inputText = "";
	private boolean isOnItemClick;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		setTitle("搜索");
		speaker = Speaker.getInstance(FuzzySearchActivity.this);
		initView();
//		emptyAdapter = new ArrayAdapter<String>(getApplication(),
//				R.layout.list_item, R.id.tv_item_name, new ArrayList<String>());
	}

	@SuppressLint("NewApi")
	private void initView() {
		lv_search_list = (ListView) findViewById(R.id.lv_search_list);
		lv_search_list.setOnItemClickListener(this);
		lv_tips_list = (ListView) findViewById(R.id.lv_tips_list);
		lv_tips_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String str = array.get(position);
				Log.d("XXXXXXXXXXX", "" + str);
				isOnItemClick = true;
				av_autotext.setText(str);
				av_autotext.setSelection(str.length());
				inputText = str;
				if (!isLoading) {
					tv_loading.setVisibility(View.VISIBLE);
					InputMethodlUtil.hideInputMethod(FuzzySearchActivity.this,
							av_autotext);
					new searchAsycTesk().execute();
				}
			}
		});
		av_autotext = (EditText) findViewById(R.id.av_autotext);

		av_autotext.addTextChangedListener(this);
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		tv_loading.setOnHoverListener(new View.OnHoverListener() {
			@Override
			public boolean onHover(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_HOVER_ENTER:
						showToast(getString(R.string.loading_hint));
						break;
				}
				return false;
			}
		});
		tv_search = (TextView) findViewById(R.id.tv_search);
		tv_search.setOnClickListener(this);
//		av_autotext.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Log.d("FuzzyActivity", "--->" + position);
//				removeListView(emptyAdapter);
//				if (!isLoading) {
//					new searchAsycTesk().execute();
//				}
//				InputMethodlUtil.hideInputMethod(FuzzySearchActivity.this,
//						av_autotext);
//			}
//		});

		av_autotext.setOnHoverListener(new OnHoverListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onHover(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_HOVER_EXIT:
					InputMethodlUtil.toggleInputMethod(
							FuzzySearchActivity.this, view);
					break;

				default:
					break;
				}
				return false;
			}
		});

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		Log.d("FuzzySearchActivity", "beforeTextChanged--->" + s.toString());
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.d("FuzzySearchActivity", "onTextChanged--->" + s.toString());
		inputText = av_autotext.getText().toString()
				.replaceAll(" +", "");
		if(!isOnItemClick){
			handler.post(flushTextChangeUi1);
			new autoAsycTesk().execute();
		}else{
			isOnItemClick = false;
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		Log.d("FuzzySearchActivity", "afterTextChanged--->" + s.toString());

	}

	Runnable flushTextChangeUi1 = new Runnable() {

		@Override
		public void run() {
			lv_search_list.setVisibility(View.GONE);
			lv_tips_list.setVisibility(View.VISIBLE);
		}
	};

	Runnable flushTextChangeUi2 = new Runnable() {

		@Override
		public void run() {
			lv_search_list.setVisibility(View.GONE);
			lv_tips_list.setVisibility(View.VISIBLE);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_search:
			InputMethodlUtil.hideInputMethod(FuzzySearchActivity.this,
					av_autotext);
//			removeListView(emptyAdapter);
			if (!isLoading) {
				tv_loading.setVisibility(View.VISIBLE);
				new searchAsycTesk().execute();
			}
			break;

		default:
			break;
		}

	}


	/**
	 * @param emptyAdapter
	 *            清空listview列表
	 */
	private void removeListView(ArrayAdapter<String> emptyAdapter) {

		lv_search_list.setAdapter(emptyAdapter);
	}

	/**
	 * @author zhou 获得搜索结果
	 */
	class autoAsycTesk extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				TatansHttp http = new TatansHttp();

				if (inputText.equals("")) {
					array.clear();
					handler.postDelayed(result2json1, 300);
					return null;
				}
				http.get(UrlUtil.AUTO_COMPLETE + inputText,
						new HttpRequestCallBack<String>() {
							@Override
							public void onLoading(long count, long current) {
								super.onLoading(count, current);
							}

							@Override
							public void onSuccess(String arg0) {
								super.onSuccess(arg0);
								json1Gson(arg0);

							}

							@Override
							public void onFailure(Throwable t, String strMsg) {
								Log.d("NetWorkError", strMsg);
								super.onFailure(t, strMsg);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		// @Override
		// protected void onPostExecute(String result) {
		// super.onPostExecute(result);
		// try {
		// JSONObject json = new JSONObject(result.toString());
		// String ok = json.getString("ok");
		// if (ok.equals("true")) {
		// JSONArray keywordsArray = json.getJSONArray("keywords");
		//
		// for (int i = 0; i < keywordsArray.length(); i++) {
		// array.add(keywordsArray.getString(i));
		// }
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

	}

	private void json1Gson(String result) {
		array.clear();
		try {
			JSONObject json = new JSONObject(result.toString());
			String ok = json.getString("ok");
			if (ok.equals("true")) {
				JSONArray keywordsArray = json.getJSONArray("keywords");

				for (int i = 0; i < keywordsArray.length(); i++) {
					array.add(keywordsArray.getString(i));
				}
			}
			showToast("共有" + array.size() + "条" + av_autotext.getText()
					+ "相关提示");
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler.postDelayed(result2json1, 300);

	}

	/**
	 * @author zhou 获得搜索结果
	 */
	class searchAsycTesk extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			TatansHttp http = new TatansHttp();
//			final String name = av_autotext.getText().toString()
//					.replaceAll(" +", "");
			if (inputText.equals("")) {
				// net.tatans.coeus.util.Speaker.getInstance(
				// getApplicationContext()).speech("请在左侧输入框输入您要搜索的内容");
				handlerSpeak.post(speakRunnable);
			} else {
				http.get(UrlUtil.FUZZY_SEARCH + inputText,
						new HttpRequestCallBack<String>() {
							@Override
							public void onStart() {
								super.onStart();
								isLoading = true;
								handler.post(flushUi1);
							}

							@Override
							public void onSuccess(String arg0) {
								super.onSuccess(arg0);
								isLoading = false;
								handler.post(flushUi2);
								json2Gson(arg0, inputText);
							}

							@Override
							public void onFailure(Throwable t, String strMsg) {
								showToast(AppConstants.FAILED_TO_REQUEST_DATA);
								tv_loading.setVisibility(View.VISIBLE);
								tv_loading
										.setText(AppConstants.FAILED_TO_REQUEST_DATA);
								isLoading = false;
								Log.d("NetWorkError", strMsg);
								super.onFailure(t, strMsg);
							}
						});
			}
			return null;
		}

	}

	Runnable flushUi1 = new Runnable() {

		@Override
		public void run() {
			lv_search_list.setVisibility(View.GONE);
			lv_tips_list.setVisibility(View.GONE);
			tv_loading.setVisibility(View.VISIBLE);
		}
	};

	Runnable flushUi2 = new Runnable() {

		@Override
		public void run() {
			lv_search_list.setVisibility(View.VISIBLE);
			lv_tips_list.setVisibility(View.GONE);
		}
	};

	Runnable speakRunnable = new Runnable() {

		@Override
		public void run() {
			showToast("请在左侧输入框输入您要搜索的内容");
		}
	};

	private void json2Gson(String result, String name) {

		try {
			JSONObject json = new JSONObject(result.toString());
			String ok = json.getString("ok");
			if (ok.equals("true")) {
				JSONArray arrayBooks = json.getJSONArray("books");
				int lenght = arrayBooks.length();
				isSearch = true;
				searchList.clear();
				ClassificatList.clear();
				if (lenght == 0) {
					handler.post(result2json);
					showToast("没有找到" + name + "相关的小说");
					return;
				}
				/* 不能在for循环之外new一个对象，这会造成添加到List中的为同一个对象 */
				BookListDto cTwoDto = null;
				for (int i = 0; i < lenght; i++) {
					cTwoDto = new BookListDto();
					cTwoDto.set_id(arrayBooks.getJSONObject(i).getString("_id"));
					String title = arrayBooks.getJSONObject(i).getString(
							"title");
					String author = arrayBooks.getJSONObject(i).getString(
							"author");
					String shortIntro = arrayBooks.getJSONObject(i).getString(
							"shortIntro");
					String latelyFollower = arrayBooks.getJSONObject(i)
							.getString("latelyFollower");
					String retentionRatio = arrayBooks.getJSONObject(i)
							.getString("retentionRatio");
					if (retentionRatio.equals("null")
							|| retentionRatio.equals(null)) {
						retentionRatio = "0";
					}
					cTwoDto.setTitle(title);
					cTwoDto.setAuthor(author);
					cTwoDto.setShortIntro(shortIntro);
					cTwoDto.setLatelyFollower(latelyFollower);
					cTwoDto.setRetentionRatio(retentionRatio);
					ClassificatList.add(cTwoDto);
					searchList.add(title + ",作者：" + author + "。"
							+ latelyFollower + "人在追，" + retentionRatio
							+ "%的读者留存。");

				}
				// 总页数
				pageCount = (int) Math.ceil(searchList.size()
						/ (AppConstants.APP_PAGE_SIZE - 1));

				handler.post(result2json);
			} else {
				handler.post(result2json);
				showToast("没有找到" + name + "相关的小说");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Runnable result2json = new Runnable() {

		@Override
		public void run() {
			setListData();
		}
	};

	public void setListData() {
//		if (searchList.size() == 0) {
//			return;
//		}
		if (isSearch&&searchList.size() != 0) {
			showToast("已经为您找到" + searchList.size() + "本与"
					+ av_autotext.getText() + "相关的小说");
			isSearch = false;
		}
		bookListAdapter = new BookListAdapter(getApplication(), ClassificatList);
		lv_search_list.setAdapter(bookListAdapter);
		isLoading = false;
		tv_loading.setVisibility(View.VISIBLE);
	}

	@Override
	public void up() {
		// if (pageCount > 1) {
		// TatansToast.showAndCancel(this, "双指左右滑动可翻页");
		// }
	}

	@Override
	public void left() {
		// currentPage++;
		// if (currentPage > pageCount) {
		// currentPage = pageCount;
		// showToast("没有下一页了");
		// } else {
		// setListData();
		//
		// }

	}

	@Override
	public void right() {
		// currentPage--;
		// if (currentPage < 1) {
		// currentPage = 1;
		// showToast("没有上一页了");
		//
		// } else {
		// setListData();
		//
		// }
	}

	@Override
	public void down() {
		// if (pageCount > 1) {
		// TatansToast.showAndCancel(this, "双指左右滑动可翻页");
		// }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
				Intent intent = new Intent();
				intent.setClass(FuzzySearchActivity.this, BookBriefActivity.class);
				intent.putExtra("title", ClassificatList.get(position).getTitle());
				intent.putExtra("_id", ClassificatList.get(position).get_id());
				startActivity(intent);

	}

	// private void hideKeyboard() {
	// ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
	// .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
	// InputMethodManager.HIDE_NOT_ALWAYS);
	// }

	Runnable result2json1 = new Runnable() {

		@Override
		public void run() {
			setData();
		}
	};

	private void setData() {
		listAdapter = new TitleAdapter(getApplicationContext(), array);
		lv_tips_list.setAdapter(listAdapter);
	}

	// private void speechShow(String text) {
	// interruptTalkback(FuzzySearchActivity.this);
	// if (speaker == null) {
	// speaker = Speaker.getInstance(FuzzySearchActivity.this);
	// }
	// speaker.speech(text);
	// }

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}

	@Override
	protected void onStop() {
		super.onStop();
		TatansToast.cancel();
	}

	@Override
	protected void onPause() {
		super.onPause();
		InputMethodlUtil.hideInputMethod(FuzzySearchActivity.this, av_autotext);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		InputMethodlUtil.hideInputMethod(FuzzySearchActivity.this, av_autotext);

	}

	// 打断talkback
	public void interruptTalkback(Context context) {
		AccessibilityManager accessibilityManager = (AccessibilityManager) context
				.getSystemService(Context.ACCESSIBILITY_SERVICE);
		if (accessibilityManager.isEnabled()) {
			accessibilityManager.interrupt();
			Log.e("hhh", "打断talkback");
		} else {
			Log.e("hhh", "不打断打断talkback");
		}

	}

}
