package net.tatans.coeus.novel.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.RankAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.RankDto;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.util.Speaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 排行榜界面
 * 
 * @author shiyunfei
 * 
 */
public class RankActivity extends BaseActivity {
	ArrayList<RankDto> al_rankingList = new ArrayList<RankDto>();
	Gson gson = new Gson();
	private ListView lv_main;
	private TextView tv_loading;
	String sRequest;
	RankAdapter adapter;
	private Speaker speaker;
	private int pageCount;
	private int currentPage = 1;
	Handler handler = new Handler();
	private boolean isSpeak;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		speaker = Speaker.getInstance(getApplicationContext());
		setContentView(R.layout.list);
		setTitle("排行榜");
		lv_main = (ListView) findViewById(R.id.lv_main);
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		new myAsycTesk().execute();

	}

	/**
	 * 网络请求
	 */
	class myAsycTesk extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			TatansHttp http = new TatansHttp();
			http.get(UrlUtil.RANK, new HttpRequestCallBack<String>() {
				@Override
				public void onLoading(long count, long current) {
					super.onLoading(count, current);
				}

				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					lv_main.setVisibility(View.VISIBLE);
					json2Gson(arg0);
//					handler.post(result2json);

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

	/**
	 * 解析json
	 */
	private void json2Gson(String result) {
		try {
			JSONObject mresult = new JSONObject(result);
			String ok = mresult.getString("ok");
			if (ok.equals("true")) {
				JSONArray array = mresult.getJSONArray("rankings");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					RankDto item = gson.fromJson(obj.toString(), RankDto.class);
					al_rankingList.add(item);
				}
			}
			pageCount = (int) Math.ceil(al_rankingList.size()
					/ AppConstants.APP_PAGE_SIZE);
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
			Speaker.getInstance(getApplicationContext()).speech("没有下一页了");
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
			Speaker.getInstance(getApplicationContext()).speech("没有上一页了");
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

	Runnable result2json = new Runnable() {

		@Override
		public void run() {
//			showToast("排行榜，当前所在第" + currentPage + "页，共" + pageCount + "页");
			setData();
		}
	};

	/**
	 * 配置适配器加载数据
	 */
	private void setData() {
		if (al_rankingList.size() == 0) {
			return;
		}
		int to = (int) (AppConstants.APP_PAGE_SIZE + AppConstants.APP_PAGE_SIZE
				* (currentPage - 1));
		int from = (int) (AppConstants.APP_PAGE_SIZE * (currentPage - 1));
		if (to > al_rankingList.size()) {
			to = al_rankingList.size();
		}
		al_rankingList.remove(2);
		adapter = new RankAdapter(al_rankingList,
				this.getApplicationContext(), (int) AppConstants.APP_PAGE_SIZE);
		lv_main.setAdapter(adapter);
	}

	//
	// private void speechShow(String text) {
	// // if (speaker == null) {
	// // speaker = Speaker.getInstance(RankActivity.this);
	// // }
	// // speaker.speech(text);
	// Toast toast = Toast.makeText(getApplicationContext(), text, 100);
	// toast.show();
	//
	// }

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (isSpeak) {
//			setTitle("排行榜");
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
