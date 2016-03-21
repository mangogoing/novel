package net.tatans.coeus.novel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class LocalFileActivity extends BaseActivity implements OnItemClickListener {
	private List<String> mainList;
	private ListView lv_local_file;
	private ArrayAdapter<String> listAdapter;
	public static final int FILE_RESULT_CODE = 1;
	private TextView tv_choose_file;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_file);
		lv_local_file = (ListView) findViewById(R.id.lv_local_file);
		tv_choose_file = (TextView) findViewById(R.id.fileText);
		setTitle("导入本地小说");
		mainList = new ArrayList<String>();
		mainList.add("智能扫描");
		mainList.add("手动选择");
		listAdapter = new ArrayAdapter<String>(getApplication(),
				R.layout.list_item, R.id.tv_item_name, mainList);
		lv_local_file.setAdapter(listAdapter);
		lv_local_file.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent;

		switch (position) {

		case 0:
			intent = new Intent(getApplicationContext(), ScanActivity.class);
			startActivity(intent);
			break;

		case 1:
			intent = new Intent(LocalFileActivity.this, MyFileManager.class);
			startActivityForResult(intent, FILE_RESULT_CODE);
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (FILE_RESULT_CODE == requestCode) {
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				tv_choose_file.setText("选择文件夹为：" + bundle.getString("file"));
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
