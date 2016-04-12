package net.tatans.coeus.novel.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.TitleAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.AssortDto;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.speaker.Speaker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FemaleAssortActivity extends BaseActivity implements
		OnItemClickListener {
	private List<String> oneList;
	private ListView lv_one_list;
	private TextView tv_loading;
	private TitleAdapter listAdapter;
	private int pageCount;
	private int currentPage = 1;
	List<AssortDto> ClassificatList = new ArrayList<AssortDto>();
	Handler handler = new Handler();
	private Speaker speaker;
	private boolean isSpeak;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		lv_one_list = (ListView) findViewById(R.id.lv_main);
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
		setTitle("女生分类");
		oneList = new ArrayList<String>();
		new myAsycTesk().execute();
		lv_one_list.setOnItemClickListener(this);

	}

	class myAsycTesk extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			TatansHttp http = new TatansHttp();
			http.get(UrlUtil.CATS, new HttpRequestCallBack<String>() {
				@Override
				public void onLoading(long count, long current) {
					super.onLoading(count, current);
				}

				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					lv_one_list.setVisibility(View.VISIBLE);
					json2Gson(arg0);
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
		ClassificatList = new ArrayList<AssortDto>();
		try {

			JSONObject json = new JSONObject(result.toString());
			String ok = json.getString("ok");
			if (ok.equals("true")) {
				/* 不能在for循环之外new一个对象，这会造成添加到List中的为同一个对象 */
				AssortDto cOneDto = null;
				JSONArray arrayFemale = json.getJSONArray("female");
				int lenghtFemale = arrayFemale.length();
				/* 不能在for循环之外new一个对象，这会造成添加到List中的为同一个对象 */
				for (int i = 0; i < lenghtFemale; i++) {
					cOneDto = new AssortDto();
					String name = arrayFemale.getJSONObject(i)
							.getString("name");
					String bookCount = arrayFemale.getJSONObject(i).getString(
							"bookCount");
					cOneDto.setName(name);
					cOneDto.setBookCount(bookCount);
					ClassificatList.add(cOneDto);
					oneList.add(name + ",共" + bookCount + "本");
				}

				// 总页数
				pageCount = (int) Math.ceil(oneList.size()
						/ AppConstants.APP_PAGE_SIZE);
				handler.post(result2json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Runnable result2json = new Runnable() {

		@Override
		public void run() {
//			showToast("女生分类，当前所在第" + currentPage + "页，共" + pageCount + "页");
			setListData();
		}
	};

	public void setListData() {
		if (oneList.size() == 0) {
			return;
		}
		int to = (int) (AppConstants.APP_PAGE_SIZE + AppConstants.APP_PAGE_SIZE
				* (currentPage - 1));
		int from = (int) (AppConstants.APP_PAGE_SIZE * (currentPage - 1));
		if (to > oneList.size()) {
			to = oneList.size();
		}
//		listAdapter = new ArrayAdapter<String>(getApplication(),
//				R.layout.list_item, R.id.tv_item_name,
//				oneList);
		listAdapter = new TitleAdapter(getApplicationContext(), oneList);
		lv_one_list.setAdapter(listAdapter);
		lv_one_list.setAdapter(listAdapter);

	}

	/*@Override
	public void up() {
		if(pageCount>1){
			TatansToast.showAndCancel(this, "双指左右滑动可翻页");
		}
	}

	@Override
	public void left() {
		currentPage++;
		if (currentPage > pageCount) {
			currentPage = pageCount;
			showToast("没有下一页了");
		} else {
			showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
			setListData();

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
			setListData();

		}
	}

	@Override
	public void down() {
		if(pageCount>1){
			TatansToast.showAndCancel(this, "双指左右滑动可翻页");
		}
	}*/

	// private void speechShow(String text) {
	// if (speaker == null) {
	// speaker = Speaker.getInstance(FemaleAssortActivity.this);
	// }
	// speaker.speech(text);
	// }

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int current =  position;
		Intent intent = new Intent();
		intent.setClass(FemaleAssortActivity.this, BookListActivity.class);
		intent.putExtra("name", ClassificatList.get(position).getName());
		startActivity(intent);

	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (isSpeak) {
//			setTitle("女生分类");
//		} else {
//			isSpeak = true;
//		}

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
