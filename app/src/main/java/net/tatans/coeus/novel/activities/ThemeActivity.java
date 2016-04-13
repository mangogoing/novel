package net.tatans.coeus.novel.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.ThemeAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.ThemeDto;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.speaker.Speaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 主题界面
 * 
 * @author shiyunfei
 * 
 */
public class ThemeActivity extends BaseActivity {
	ArrayList<ThemeDto> al_BooksTheme = new ArrayList<ThemeDto>();
	Gson gson = new Gson();
	private ListView lv_main;
	private TextView tv_loading;
	int start = 0;
	ThemeAdapter adapter;
	private Speaker speaker;
	private int pageCount;
	private int currentPage = 1;
	Handler handler = new Handler();
	String url, title, sRequest;
	private boolean isSpeak;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		lv_main = (ListView) findViewById(R.id.lv_main);
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
		new myAsycTesk().execute();
		init();
		setTitle(title);
		setData();
	}

	/**
	 * 三个主题网络请求分发，json格式一样，可统一处理
	 */
	private void init() {
		switch (getIntent().getStringExtra("theme")) {
		case "HOT_WEEKLY":
			url = UrlUtil.HOT_WEEKLY + start;
			title = "一周最热";
			break;
		case "RECENT_PUBLISH":
			url = UrlUtil.RECENT_PUBLISH + start;
			title = "最新发布";
			break;
		case "MAX_COLLECTOR":
			url = UrlUtil.MAX_COLLECTOR + start;
			title = "最多收藏";
			break;
		default:
			break;
		}
	}

	class myAsycTesk extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			TatansHttp http = new TatansHttp();
			http.get(url, new HttpRequestCallBack<String>() {
				@Override
				public void onLoading(long count, long current) {
					super.onLoading(count, current);
				}

				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					json2Gson(arg0);
					handler.post(result2json);

				}

				@Override
				public void onFailure(Throwable t, String strMsg) {
					showToast(AppConstants.FAILED_TO_REQUEST_DATA);
					tv_loading.setText(AppConstants.FAILED_TO_REQUEST_DATA);
					Log.d("NetWorkError", strMsg);
					super.onFailure(t, strMsg);
				}
			});
			return null;
		}
	}

	private void json2Gson(String result) {
		try {
			JSONObject mresult = new JSONObject(result);
			JSONArray array = mresult.getJSONArray("bookLists");
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				ThemeDto item = gson.fromJson(obj.toString(), ThemeDto.class);
				al_BooksTheme.add(item);
			}
			pageCount = (int) Math.ceil(al_BooksTheme.size()
					/ AppConstants.APP_PAGE_SIZE);

//			showToast(title + ",当前所在第" + currentPage + "页，共" + pageCount + "页");
			setData();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/*@Override
	public void left() {
		currentPage++;
		if (currentPage > pageCount) {
			currentPage = pageCount;
			showToast("没有下一页了");
		} else {
			showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
			setData();
		}

	}

	@Override
	public void right() {
		currentPage--;
		if (currentPage < 1) {
			currentPage = 1;
			showToast("没有上一页了");
		} else {
			showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
			setData();
		}

	}

	@Override
	public void up() {
		if (pageCount > 1) {
			TatansToast.showAndCancel(this, "双指左右滑动可翻页");
		}

	}

	@Override
	public void down() {
		if (pageCount > 1) {
			TatansToast.showAndCancel(this, "双指左右滑动可翻页");
		}

	}
*/
	Runnable result2json = new Runnable() {

		@Override
		public void run() {
			setData();
		}
	};

	private void setData() {
		if (al_BooksTheme.size() == 0) {
			return;
		}
		int to = (int) (AppConstants.APP_PAGE_SIZE + AppConstants.APP_PAGE_SIZE
				* (currentPage - 1));
		int from = (int) (AppConstants.APP_PAGE_SIZE * (currentPage - 1));
		if (to > al_BooksTheme.size()) {
			to = al_BooksTheme.size();
		}
		adapter = new ThemeAdapter(al_BooksTheme,
				this.getApplicationContext(), (int) AppConstants.APP_PAGE_SIZE);
		lv_main.setAdapter(adapter);
		tv_loading.setVisibility(View.GONE);
	}

	// private void speechShow(String text) {
	// if (speaker == null) {
	// speaker = Speaker.getInstance(ThemeActivity.this);
	// }
	// speaker.speech(text);
	// }

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
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

//	@Override
//	protected void onResume() {
//		super.onResume();
//		if (isSpeak) {
//			setTitle(title);
//		} else {
//			isSpeak = true;
//		}
//
//	}

}
