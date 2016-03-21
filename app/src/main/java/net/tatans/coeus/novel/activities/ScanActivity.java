package net.tatans.coeus.novel.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.dto.ScanDto;
import net.tatans.coeus.novel.tools.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("HandlerLeak")
public class ScanActivity extends BaseActivity implements OnItemClickListener {
	private String[] fileName;
	private String[] fileSize;
	private String[] filePath;
	private ListView lv_scan;
	private TextView tv_loading;
	private ArrayList<ScanDto> scanList = new ArrayList<>();
	private List<Map<String, Object>> listems;
	private long fileNumber = 0;
	private int txtFileNumber = 0;
	private int flag = 0;
	private TatansDb db;
	private ProgressDialog dialog;
	private int count;
	private String mFileName;
	private int currentPage = 1;
	private int pageCount;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				/*
				 * SimpleAdapter的参数说明 第一个参数 表示访问整个android应用程序接口，基本上所有的组件都需要
				 * 第二个参数表示生成一个Map(String ,Object)列表选项 第三个参数表示界面布局的id
				 * 表示该文件作为列表项的组件 第四个参数表示该Map对象的哪些key对应value来生成列表项 第五个参数表示来填充的组件
				 * Map对象key对应的资源一依次填充组件 顺序有对应关系 注意的是map对象可以key可以找不到 但组件的必须要有资源填充
				 * 因为 找不到key也会返回null 其实就相当于给了一个null资源 下面的程序中如果 new String[] {
				 * "name", "head", "desc","name" } new int[]
				 * {R.id.name,R.id.head,R.id.desc,R.id.head} 这个head的组件会被name资源覆盖
				 */
				setListData();
				tv_loading.setVisibility(View.GONE);
			} else if (msg.what == 300) {
				flag++;
				if (flag % 100 == 0) {
					tv_loading.setText("已扫描" + fileNumber + "个文件夹，" + "找到"
							+ txtFileNumber + "个文本文件");
				}
			} else if (msg.what == 400) {
				save(mFileName, mFileName);
			}

			setTitle("扫描结果");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan);
		db = TatansDb.create( AppConstants.TATANS_DB_NAME);
		initDialog();
		lv_scan = (ListView) findViewById(R.id.lv_scan);
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		lv_scan.setOnItemClickListener(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				getTxtFile();
			}

		}).start();

	}

	private void initDialog() {
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
		dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
		dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		// 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
		dialog.setTitle("正在导入，请稍后");

	}

	private void loadNovel(String sourceFilePath, String novelName,
			String targetDirectory) {
		mHandler.sendEmptyMessage(400);
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

	/**
	 * 记录播放历史，若为收藏的小说，保存章节位置
	 * 
	 * @author shiyunfei
	 */
	@SuppressWarnings("unused")
	private void save(Map<String, Object> map) {
		String filePath = (String) map.get("filePath");
		String fileName = (String) map.get("fileName");
		CollectorDto collerctor = db.findById(filePath, CollectorDto.class);
		if (collerctor == null) {
			// 加入到书藏书籍数据库
			CollectorDto collector = new CollectorDto(filePath, fileName, 0, 3,
					new Date(), 1, 0, -1, 0, "local");
			if (collerctor == null) {
				db.save(collector);
			}
			TatansToast.showAndCancel( fileName + "导入书架成功");
		} else {
			TatansToast.showAndCancel( "你已经导入过该小说");
		}

	}

	protected void getTxtFile() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();// 获得SD卡路径
			// File path = new File("/mnt/sdcard/");
			File[] files = path.listFiles();// 读取
			File[] tempArr = new File[files.length - 1];
			int i = 0;
			for (File s : files) {
				if (!s.getAbsolutePath().equals(
						path.getAbsolutePath() + "/tatans")) {
					tempArr[i] = s;
					i++;
				}
			}
			getFileName(tempArr);
		}
		// 这里就会自动根据规则进行排序
		Collections.sort(scanList, comparator);

		int size = scanList.size();
		ArrayList<String> nameList = new ArrayList<>();
		ArrayList<String> sizeList = new ArrayList<>();
		ArrayList<String> pathList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			nameList.add(scanList.get(i).getSingleName());
			sizeList.add(scanList.get(i).getSingleSize());
			pathList.add(scanList.get(i).getPath());
		}
		fileName = (String[]) nameList.toArray(new String[size]);
		fileSize = (String[]) sizeList.toArray(new String[size]);
		filePath = (String[]) pathList.toArray(new String[size]);
		listems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < size; i++) {
			Map<String, Object> listem = new HashMap<String, Object>();
			listem.put("fileName", fileName[i]);
			listem.put("fileSize", fileSize[i]);
			listem.put("filePath", filePath[i]);
			listems.add(listem);
		}
		pageCount = (int) Math.ceil((listems.size())
				/ AppConstants.APP_PAGE_SIZE);
		mHandler.sendEmptyMessage(200);

	}

	@SuppressWarnings({ "resource" })
	private void getFileName(File[] files) {
		if (files != null) {// 先判断目录是否为空，否则会报空指针

			for (File file : files) {
				fileNumber++;
				if (file.isDirectory()) {
					Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
							+ file.getPath().toString());

					getFileName(file.listFiles());
					Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString()
							+ file.getPath().toString());
				} else {
					String fileName = file.getName();
					ScanDto scanDto;
					if (fileName.endsWith(".txt")) {
						try {
							FileInputStream fis = null;
							fis = new FileInputStream(file);
							double fileSize = fis.available();
							String singleName = fileName.substring(0,
									fileName.lastIndexOf(".")).toString();
							if (fileSize > 1024 * 100
									|| isContainChinese(singleName)) {
								scanDto = new ScanDto();
								txtFileNumber++;

								scanDto.setSingleName(singleName);
								scanDto.setPath(file.getPath());

								scanDto.setFileSize(fileSize);
								String singleSize;
								if (fileSize < 1024) {

									singleSize = fileSize + "b";
								} else if (fileSize / (1024 * 1024) > 1) {
									BigDecimal b = new BigDecimal(fileSize
											/ (1024 * 1024));
									double d = b.setScale(2,
											BigDecimal.ROUND_HALF_UP)
											.doubleValue();
									singleSize = d + "mb";
								} else {
									BigDecimal b = new BigDecimal(
											fileSize / 1024);
									double d = b.setScale(2,
											BigDecimal.ROUND_HALF_UP)
											.doubleValue();
									singleSize = d + "kb";
								}
								scanDto.setSingleSize(singleSize);
								scanList.add(scanDto);
							}

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}

				mHandler.sendEmptyMessage(300);
			}

		}
	}

	public static boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	Comparator<ScanDto> comparator = new Comparator<ScanDto>() {
		public int compare(ScanDto s1, ScanDto s2) {
			// 先排年龄
			// if(s1.getFileSize()!=s2.getFileSize()){
			return (int) (s2.getFileSize() - s1.getFileSize());
			// }
			// return fileNumber;

		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		// save(listems.get(position));
		mFileName = (String) listems.get(position).get("fileName");
		final String mFilePath = (String) listems.get(position).get("filePath");
		final String targetDirectory = Environment
				.getExternalStorageDirectory() + "/tatans/novel/" + mFileName;
		if (hasCollectored(mFileName)) {
			TatansToast.showAndCancel( "你已经导入过该小说");
		} else {
			dialog.show();
			new Thread(new Runnable() {

				@Override
				public void run() {

					loadNovel(mFilePath, mFileName, targetDirectory);
				}

			}).start();
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
	public void up() {
		// TODO Auto-generated method stub

	}

	@Override
	public void down() {
		// TODO Auto-generated method stub

	}

	public void setListData() {
		if (listems.size() == 0) {
			return;
		}
		int to = (int) (AppConstants.APP_PAGE_SIZE + AppConstants.APP_PAGE_SIZE
				* (currentPage - 1));
		int from = (int) (AppConstants.APP_PAGE_SIZE * (currentPage - 1));
		if (to > listems.size()) {
			to = listems.size();
		}

		SimpleAdapter simplead = new SimpleAdapter(ScanActivity.this,
				listems.subList(from, to), R.layout.scan_item, new String[] {
						"fileName", "fileSize", "filePath" }, new int[] {
						R.id.name, R.id.size, R.id.path });
		lv_scan.setAdapter(simplead);
	}

	private void showToast(String text) {
		TatansToast.showAndCancel( text);
	}

}
