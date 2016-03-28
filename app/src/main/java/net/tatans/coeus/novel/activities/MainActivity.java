package net.tatans.coeus.novel.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.MainAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.speech.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnItemClickListener {
	private List<String> mainList;
	private ListView lv_main;
	private TextView tx_disclaimer, tv_local;
	private MainAdapter listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		lv_main = (ListView) findViewById(R.id.lv_novel_mian);
		tx_disclaimer = (TextView) findViewById(R.id.tx_disclaimer);
		tv_local = (TextView) findViewById(R.id.tv_local);
		setTitle("天坦小说");
		mainList = new ArrayList<String>();
		mainList.add("搜索");
		mainList.add("我的书架");
		mainList.add("排行榜");
		mainList.add("本周最热主题");
		mainList.add("最新发布主题");
		mainList.add("最多收藏主题");
		mainList.add("男生分类");
		mainList.add("女生分类");
//		listAdapter = new ArrayAdapter<String>(getApplication(),
//				R.layout.list_item, R.id.tv_item_name, mainList);
		listAdapter = new MainAdapter(getApplicationContext(), mainList);
		lv_main.setAdapter(listAdapter);
		lv_main.setOnItemClickListener(this);
		tx_disclaimer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						DisclaimerActivity.class);
				startActivity(intent);
			}
		});
//		tv_local.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, LocalFileActivity.class);
//				startActivity(intent);
//			}
//		});

	}
	
	

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent;
		if (position == 1) {
			intent = new Intent(getApplicationContext(),
					CollectorActivity.class);
			startActivity(intent);
		} else {
			if (!NetWorkUtil.hasNetworkConnection(MainActivity.this)) {
				TatansToast.showAndCancel("当前网络未连接，请检查网络。");
				return;
			}
			switch (position) {
			/**
			 * 搜索
			 */
			case 0:
				intent = new Intent(getApplicationContext(),
						FuzzySearchActivity.class);
				startActivity(intent);
				break;
			/**
			 * 排行榜
			 */
			case 2:
				intent = new Intent(getApplicationContext(), RankActivity.class);
				startActivity(intent);
				break;
			/**
			 * 本周最热主题
			 */
			case 3:
				intent = new Intent(getApplicationContext(),
						ThemeActivity.class);
				intent.putExtra("theme", "HOT_WEEKLY");
				startActivity(intent);
				break;
			/**
			 * 最新发布
			 */
			case 4:
				intent = new Intent(getApplicationContext(),
						ThemeActivity.class);
				intent.putExtra("theme", "RECENT_PUBLISH");
				startActivity(intent);
				break;
			/**
			 * 最多收藏
			 */
			case 5:
				intent = new Intent(getApplicationContext(),
						ThemeActivity.class);
				intent.putExtra("theme", "MAX_COLLECTOR");
				startActivity(intent);
				break;
			case 6:
				intent = new Intent();
				intent.setClass(MainActivity.this, MaleAssortActivity.class);
				startActivity(intent);
				break;
			case 7:
				intent = new Intent();
				intent.setClass(MainActivity.this, FemaleAssortActivity.class);
				startActivity(intent);
				break;

			}

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
