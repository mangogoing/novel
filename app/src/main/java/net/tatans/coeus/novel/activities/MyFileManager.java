package net.tatans.coeus.novel.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.MyAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.dto.StorageInfo;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyFileManager extends BaseActivity implements OnItemClickListener {
	private ListView lv_file_select;
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/storage";
	private String curPath;
	private TextView mPath;
	List<StorageInfo> list;
	private TatansDb db;
	private ProgressDialog dialog;
	private int count;
	private String fileName;
	private int currentPage = 1;
	private int pageCount;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 300) {
				save(fileName, fileName);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fileselect);
		setTitle("本地目录");
		db = TatansDb.create( AppConstants.TATANS_DB_NAME);
		mPath = (TextView) findViewById(R.id.mPath);
		lv_file_select = (ListView) findViewById(R.id.lv_file_select);
		lv_file_select.setOnItemClickListener(this);
		list = StorageUtil.listAvaliableStorage(getApplicationContext());
		getFileDir(rootPath);
		initDialog();
	}

	private void initDialog() {
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
		dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
		dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		// 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
		dialog.setTitle("正在导入，请稍后");
	}

	private void getFileDir(String filePath) {
		mPath.setText("本地目录");
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filePath);
		File[] files = f.listFiles();

		List<File> list1 = new ArrayList<File>();
		List<File> list2 = new ArrayList<File>();
		List<File> list3 = new ArrayList<File>();
		for (File file : files) {
			if (f.isDirectory()) {
				list1.add(file);
			}
			if (f.isFile()) {
				list2.add(file);
			}
		}
		Collections.sort(list1);
		Collections.sort(list2);
		list3.addAll(list1);
		list3.addAll(list2);
		File[] filesNew = (File[]) list3.toArray(new File[list3.size()]);

		if (!filePath.equals(rootPath)) {
			items.add("b1");
			paths.add(rootPath);
			items.add("b2");
			paths.add(f.getParent());
		}
		for (int i = 0; i < filesNew.length; i++) {
			File file = filesNew[i];
			if (filePath.equals(rootPath)) {
				if (list.size() == 1) {
					if (file.getName().equals("sdcard0")) {
						items.add(file.getName());
						paths.add(file.getPath());
					}

				} else {
					if (file.getName().equals("sdcard0")
							|| file.getName().equals("sdcard1")) {
						items.add(file.getName());
						paths.add(file.getPath());
					}
				}

			} else {
				// check file is shp file or not
				// if is,add into list to show
				if (checkShapeFile(file)) {
					items.add(file.getName());
					paths.add(file.getPath());
				}
			}
		}
		pageCount = (int) Math
				.ceil((items.size()) / AppConstants.APP_PAGE_SIZE);
		setListData();
	}

	// // open allocate file
	// @Override
	// protected void onItemClick(ListView l, View v, int position, long id) {
	// final File file = new File(paths.get(position));
	// if (file.isDirectory()) {
	// curPath = paths.get(position);
	// getFileDir(curPath);
	// } else {
	// // Intent data = new Intent(MyFileManager.this,
	// // LocalFileActivity.class);
	// // Bundle bundle = new Bundle();
	// // bundle.putString("file", file.getPath());
	// // data.putExtras(bundle);
	// // setResult(2, data);
	// // finish();
	// // save(file.getPath(), file.getName());
	// fileName = file.getName().replace(".txt", "");
	// final String targetDirectory = Environment
	// .getExternalStorageDirectory()
	// + "/tatans/novel/"
	// + fileName;
	// final String filePath = file.getPath();
	// if (hasCollectored(fileName)) {
	// TatansToast.showAndCancel(MyFileManager.this, "你已经导入过该小说");
	// } else {
	// dialog.show();
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	//
	// loadNovel(filePath, fileName, targetDirectory);
	// }
	//
	// }).start();
	// }
	//
	// }
	// }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final File file = new File(paths.get(position));
		if (file.isDirectory()) {
			curPath = paths.get(position);
			getFileDir(curPath);
		} else {
			// Intent data = new Intent(MyFileManager.this,
			// LocalFileActivity.class);
			// Bundle bundle = new Bundle();
			// bundle.putString("file", file.getPath());
			// data.putExtras(bundle);
			// setResult(2, data);
			// finish();
			// save(file.getPath(), file.getName());
			fileName = file.getName().replace(".txt", "");
			final String targetDirectory = Environment
					.getExternalStorageDirectory()
					+ "/tatans/novel/"
					+ fileName;
			final String filePath = file.getPath();
			if (hasCollectored(fileName)) {
				TatansToast.showAndCancel( "你已经导入过该小说");
			} else {
				dialog.show();
				new Thread(new Runnable() {

					@Override
					public void run() {

						loadNovel(filePath, fileName, targetDirectory);
					}

				}).start();
			}

		}

	}

	private void loadNovel(String sourceFilePath, String novelName,
			String targetDirectory) {
		mHandler.sendEmptyMessage(300);
		count = FileUtil.divide(sourceFilePath, novelName, targetDirectory);
		
	}

	// 判断是否收藏过
	private boolean hasCollectored(String fileName) {
		CollectorDto collerctor = db.findById(fileName, CollectorDto.class);
		if (collerctor == null) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 记录播放历史，若为收藏的小说，保存章节位置
	 * 
	 * @author shiyunfei
	 */
	@SuppressWarnings("unused")
	private void save(String _id, String fileName) {
		CollectorDto collerctor = db.findById(_id, CollectorDto.class);
		// 加入到书藏书籍数据库
		CollectorDto collector = new CollectorDto(_id, fileName, 0, 3,
				new Date(), count, 0, -1, 0, "local");
		if (collerctor == null) {
			db.save(collector);
		}
		dialog.dismiss();
		TatansToast.showAndCancel( fileName + "导入书架成功");

	}

	public boolean checkShapeFile(File file) {
		String fileNameString = file.getName();
		String endNameString = fileNameString.substring(
				fileNameString.lastIndexOf(".") + 1, fileNameString.length())
				.toLowerCase();
		// file is directory or not
		if (fileNameString.lastIndexOf(".") == -1) {
			if (file.isDirectory()) {
				return true;
			} else {
				return false;
			}
		}
		if (endNameString.equals("txt")) {
			return true;
		} else {
			return false;
		}
	}

	protected final String getSDDir() {
		if (!checkSDcard()) {
			Toast.makeText(this, "no sdcard", Toast.LENGTH_SHORT).show();
			return "";
		}
		try {
			String SD_DIR = Environment.getExternalStorageDirectory()
					.toString();
			return SD_DIR;
		} catch (Exception e) {
			return "";
		}
	}

	public boolean checkSDcard() {
		String sdStutusString = Environment.getExternalStorageState();
		if (sdStutusString.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void left() {
		// currentPage++;
		// if (currentPage > pageCount) {
		// currentPage = pageCount;
		// showToast("没有下一页了");
		// } else {
		// showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
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
		// showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
		// setListData();
		//
		// }
	}

	@Override
	public void up() {

	}

	@Override
	public void down() {

	}

	public void setListData() {
//		if (items.size() == 0) {
//			return;
//		}
//		int to = (int) (AppConstants.APP_PAGE_SIZE + AppConstants.APP_PAGE_SIZE
//				* (currentPage - 1));
//		int from = (int) (AppConstants.APP_PAGE_SIZE * (currentPage - 1));
//		if (to > items.size()) {
//			to = items.size();
//		}

		lv_file_select.setAdapter(new MyAdapter(this, items, paths));
	}

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// // Do something.
	//
	// curPath = paths.get(1);
	// getFileDir(curPath);
	//
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

}
