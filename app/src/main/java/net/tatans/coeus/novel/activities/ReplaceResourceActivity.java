package net.tatans.coeus.novel.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.ChapterDto;
import net.tatans.coeus.novel.dto.SummaryDto;
import net.tatans.coeus.novel.tools.JsonUtils;
import net.tatans.coeus.novel.tools.SharedPreferencesUtil;
import net.tatans.coeus.novel.tools.UrlUtil;

import java.util.ArrayList;
import java.util.List;

public class ReplaceResourceActivity extends BaseActivity implements
		OnItemClickListener {

	private ListView lv_one_list;
	private ArrayAdapter<String> listAdapter;
	private String title;
	private int isDownLoad;
	private int currentPage = 1;
	private List<String> titleList = new ArrayList<String>();
	Handler handler = new Handler();
	private boolean isSpeak;
	private String bookId;
	private int totalChapterCount;
	private int currentPosition;
	protected List<ChapterDto> ChapterList;
	private RequestQueue mRequestQueue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		lv_one_list = (ListView) findViewById(R.id.lv_main);
		mRequestQueue = Volley.newRequestQueue(ReplaceResourceActivity.this);
		Intent intent = getIntent();
		totalChapterCount = intent.getIntExtra("totalChapterCount", 0);
		bookId = intent.getStringExtra("bookId");
		isDownLoad = intent.getIntExtra("isDownLoad", -1);
		currentPosition = intent.getIntExtra("currentPosition", 0);
		title = intent.getStringExtra("title");
		setTitle(title + "资源列表");
		lv_one_list.setOnItemClickListener(this);
		init();

	}

	private void init() {

		getSummaryResource(this, mRequestQueue, bookId);

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
						for (int i = 0; i < summarylist.size(); i++) {
							titleList.add(summarylist.get(i).getName());
						}

						int sourceNum = SharedPreferencesUtil
								.readData(ReplaceResourceActivity.this);
						if (summarylist.size() > 0) {
							if (sourceNum > summarylist.size() - 1) {
								sourceNum = summarylist.size() - 1;
							}
							showToast("当前资源" + titleList.get(sourceNum));
						}
						totalChapterCount = titleList.size();

						handler.post(result2json);

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});
		mRequestQueue.add(jr);

	}

	Runnable result2json = new Runnable() {

		@Override
		public void run() {
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

		listAdapter = new ArrayAdapter<String>(getApplication(),
				R.layout.list_item, R.id.tv_item_name, titleList);
		lv_one_list.setAdapter(listAdapter);
		lv_one_list.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SharedPreferencesUtil.saveData(this, position);
		Intent intent = new Intent();
		intent.putExtra("bookId", bookId);
		intent.putExtra("totalChapterCount", totalChapterCount);
		intent.putExtra("isDownLoad", isDownLoad);
		intent.putExtra("sourceNum", position);
		intent.putExtra("currentPosition", currentPosition);
		intent.putExtra("title", title);
		intent.setClass(ReplaceResourceActivity.this, MoreActivity.class);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void up() {
	}

	@Override
	public void left() {

	}

	@Override
	public void right() {
	}

	@Override
	public void down() {
	}

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isSpeak) {
			setTitle(title + "资源列表");
		} else {
			isSpeak = true;
		}

	}
}
