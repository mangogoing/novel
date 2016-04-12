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
import net.tatans.coeus.novel.adapter.RankListAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.RankListDto;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.speaker.Speaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 子排行榜界面
 * 
 * @author shiyunfei
 * 
 */
public class RankListActivity extends BaseActivity {
	ArrayList<RankListDto> al_Books = new ArrayList<RankListDto>();
	Gson gson = new Gson();
	private ListView lv_main;
	private TextView tv_loading;
	String sRequest;
	String id;
	RankListAdapter adapter;
	private Speaker speaker;
	private int pageCount;
	private int currentPage = 1;
	Handler handler = new Handler();
	private String title;
	private boolean isSpeak;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		id = getIntent().getStringExtra("id");
		if(id.equals("548e984e5beb6f0458d652aa")){
			id = "564eb878efe5b8e745508fde";
		}
		title = getIntent().getStringExtra("title");
		setTitle(title);
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
//		setData();
	}

	/**
	 * 解析json
	 */
	private void json2Gson(String result) {

		try {
			JSONObject mresult = new JSONObject(result);
			String ok = mresult.getString("ok");
			if (ok.equals("true")) {
				JSONArray array = mresult.getJSONObject("ranking")
						.getJSONArray("books");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					RankListDto item = gson.fromJson(obj.toString(),
							RankListDto.class);
					al_Books.add(item);
				}
			}
			pageCount = (int) Math
					.ceil(al_Books.size() / AppConstants.APP_PAGE_SIZE);
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
		if(pageCount>1){
			TatansToast.showAndCancel(this, "双指左右滑动可翻页");
		}

	}

	@Override
	public void down() {
		if(pageCount>1){
			TatansToast.showAndCancel(this, "双指左右滑动可翻页");
		}

	}*/

	private void setData() {
		if (al_Books.size() == 0) {
			showToast(AppConstants.FAILED_TO_REQUEST_DATA);
			return;
		}
		int to = (int) (AppConstants.APP_PAGE_SIZE + AppConstants.APP_PAGE_SIZE
				* (currentPage - 1));
		int from = (int) (AppConstants.APP_PAGE_SIZE * (currentPage - 1));
		if (to > al_Books.size()) {
			to = al_Books.size();
		}
		adapter = new RankListAdapter(al_Books,
				this.getApplicationContext(), (int) AppConstants.APP_PAGE_SIZE);
		lv_main.setAdapter(adapter);
	}

	class myAsycTesk extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			TatansHttp http = new TatansHttp();
			http.get(UrlUtil.RANK + id, new HttpRequestCallBack<String>() {
				@Override
				public void onLoading(long count, long current) {
					super.onLoading(count, current);
				}

				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					lv_main.setVisibility(View.VISIBLE);
					json2Gson(arg0);
					handler.post(result2json);

				}

				@Override
				public void onFailure(Throwable t, String strMsg) {
					showToast(AppConstants.FAILED_TO_REQUEST_DATA);
					tv_loading
							.setText(AppConstants.FAILED_TO_REQUEST_DATA);
					Log.d("NetWorkError", strMsg);
					super.onFailure(t, strMsg);
				}
			});
			return null;
		}
	}

	Runnable result2json = new Runnable() {

		@Override
		public void run() {
//			showToast(title + ",当前所在第" + currentPage + "页，共" + pageCount + "页");
			setData();
		}
	};

//	private void speechShow(String text) {
//		if (speaker == null) {
//			speaker = Speaker.getInstance(RankListActivity.this);
//		}
//		speaker.speech(text);
//	}

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		if (isSpeak) {
//			setTitle(title + "小说列表");
//		} else {
//			isSpeak = true;
//		}

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
