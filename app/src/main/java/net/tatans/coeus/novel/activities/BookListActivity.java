package net.tatans.coeus.novel.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import net.tatans.coeus.novel.adapter.BookListAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.BookListDto;
import net.tatans.coeus.novel.tools.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends BaseActivity implements
		OnItemClickListener {
//	private List<String> myList;
	private ListView lv_one_list;
	private BookListAdapter listAdapter;
	private TextView tv_loading;
	private String name;
	private int currentPage = 1;
	private List<BookListDto> ClassificatList = new ArrayList<BookListDto>();
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
		name = getIntent().getStringExtra("name");
		setTitle(name);
//		myList = new ArrayList<String>();
		new myAsycTesk().execute();
		lv_one_list.setOnItemClickListener(this);

	}

	class myAsycTesk extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			TatansHttp http = new TatansHttp();

			// String postfix = "start=" + (currentPage - 1)
			// * AppConstants.APP_PAGE_SIZE + "&limit="
			// + AppConstants.APP_PAGE_SIZE + "&tag=" + name;

			String postfix = "start=" + 0 + "&limit=" + 100 + "&tag=" + name;

			http.get(UrlUtil.CAT_LIST + postfix,
					new HttpRequestCallBack<String>() {
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
							tv_loading
									.setText(AppConstants.FAILED_TO_REQUEST_DATA);
							Log.d("NetWorkError", strMsg);
							super.onFailure(t, strMsg);
						}
					});
			return null;

		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
		}
	}

	private void json2Gson(String result) {

		Log.d("ClassificatTwoActivity", result + "");
//		myList.clear();
		ClassificatList.clear();
		try {
			JSONObject json = new JSONObject(result.toString());
			String ok = json.getString("ok");
			if (ok.equals("true")) {
				JSONArray arrayBooks = json.getJSONArray("books");
				int lenght = arrayBooks.length();
				/* 不能在for循环之外new一个对象，这会造成添加到List中的为同一个对象 */
				BookListDto cTwoDto = null;
				for (int i = 0; i < lenght; i++) {
					cTwoDto = new BookListDto();
					cTwoDto.set_id(arrayBooks.getJSONObject(i).getString("_id"));
					String title = arrayBooks.getJSONObject(i).getString(
							"title");
					cTwoDto.setTitle(title);
					String author = arrayBooks.getJSONObject(i).getString(
							"author");
					cTwoDto.setAuthor(author);
					String shortIntro = arrayBooks.getJSONObject(i).getString(
							"shortIntro");
					cTwoDto.setShortIntro(shortIntro);
					String latelyFollower = arrayBooks.getJSONObject(i)
							.getString("latelyFollower");
					cTwoDto.setLatelyFollower(latelyFollower);
					String retentionRatio = arrayBooks.getJSONObject(i)
							.getString("retentionRatio");
					if (retentionRatio.equals("null")
							|| retentionRatio.equals(null)) {
						retentionRatio = "0";
					}
					cTwoDto.setRetentionRatio(retentionRatio);
					ClassificatList.add(cTwoDto);
//					myList.add(title + ",作者：" + author + "。" + latelyFollower
//							+ "人在追，" + retentionRatio + "%的读者留存。");

				}
				if (currentPage == 1) {
					// showToast(name + "小说列表," + "当前所在第" + currentPage + "页");
				}
				setListData(ClassificatList);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void setListData(List<BookListDto> classificatList) {
		// listAdapter = new ArrayAdapter<String>(getApplication(),
		// R.layout.list_item, R.id.tv_item_name, list);
		listAdapter = new BookListAdapter(getApplicationContext(), classificatList);
		lv_one_list.setAdapter(listAdapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.setClass(BookListActivity.this, BookBriefActivity.class);
		intent.putExtra("title", ClassificatList.get(position).getTitle());
		intent.putExtra("_id", ClassificatList.get(position).get_id());
		startActivity(intent);

	}

	@Override
	public void up() {
		// TatansToast.showAndCancel(this, "双指左右滑动可翻页");
	}

	@Override
	public void left() {
		// currentPage++;
		// showToast("当前所在第" + currentPage + "页");
		// new myAsycTesk().execute();

	}

	@Override
	public void right() {
		// currentPage--;
		// if (currentPage < 1) {
		// currentPage = 1;
		// showToast("没有上一页了");
		// } else {
		// showToast("当前所在第" + currentPage + "页");
		// new myAsycTesk().execute();
		//
		// }
	}

	@Override
	public void down() {
		// TatansToast.showAndCancel(this, "双指左右滑动可翻页");
	}

	// private void speechShow(String text) {
	// if (speaker == null) {
	// speaker = Speaker.getInstance(BookListActivity.this);
	// }
	// speaker.speech(text);
	// // Toast toast = Toast.makeText(getApplicationContext(), text,
	// // Toast.LENGTH_SHORT);
	// // toast.show();
	// }

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (isSpeak) {
		// setTitle(name + "小说列表");
		// } else {
		// isSpeak = true;
		// }

	}

	@Override
	protected void onStop() {
		super.onStop();
		TatansToast.cancel();
	}

}
