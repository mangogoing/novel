package net.tatans.coeus.novel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ThemeMainActivity extends BaseActivity implements
		OnItemClickListener {
	private List<String> mainList;
	private ListView lv_main;
	private ArrayAdapter<String> listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.theme_main);
		lv_main = (ListView) findViewById(R.id.lv_novel_mian);
		setTitle("主题");

		mainList = new ArrayList<String>();

		mainList.add("本周最热主题");
		mainList.add("最新发布主题");
		mainList.add("最多收藏主题");

		listAdapter = new ArrayAdapter<String>(getApplication(),
				R.layout.list_item, R.id.tv_item_name, mainList);
		lv_main.setAdapter(listAdapter);
		lv_main.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent;

		switch (position) {

		/**
		 * 本周最热主题
		 */
		case 0:
			intent = new Intent(getApplicationContext(), ThemeActivity.class);
			intent.putExtra("theme", "HOT_WEEKLY");
			startActivity(intent);
			break;
		/**
		 * 最新发布
		 */
		case 1:
			intent = new Intent(getApplicationContext(), ThemeActivity.class);
			intent.putExtra("theme", "RECENT_PUBLISH");
			startActivity(intent);
			break;
		/**
		 * 最多收藏
		 */
		case 2:
			intent = new Intent(getApplicationContext(), ThemeActivity.class);
			intent.putExtra("theme", "MAX_COLLECTOR");
			startActivity(intent);
			break;

		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void left() {

	}

	@Override
	public void right() {

	}

	@Override
	public void up() {

	}

	@Override
	public void down() {

	}

}
