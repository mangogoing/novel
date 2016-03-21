package net.tatans.coeus.novel.app;

import android.content.IntentFilter;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.util.Speaker;

public class App extends TatansApplication {
	private static App instance = null;
	private static Speaker speaker;
	public TatansDb db;

	// MyDownLoadProgressReciver progressReciver ;

	public static App getInstance() {
		if (instance == null) { // line 12
			instance = new App(); // line 13
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/*
		 * db = TatansDb.create(this, "MyCollector"); if(progressReciver==null){
		 * progressReciver=new MyDownLoadProgressReciver(); }
		 */
		speaker = Speaker.getInstance(getApplicationContext());
		IntentFilter filter = new IntentFilter(UrlUtil.FINISH_ACTION);
		// this.registerReceiver(progressReciver, filter);
	}

	/*
	 * private class MyDownLoadProgressReciver extends BroadcastReceiver {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { //
	 * 停止下载service Intent Intent = new Intent(getApplicationContext(),
	 * DownLoadService.class); stopService(Intent); if
	 * (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
	 * TatansToast.showAndCancel(getApplicationContext(), "您的网络已断开，缓存过程被终止"); }
	 * else { TatansToast.showAndCancel(getApplicationContext(), "缓存完成");
	 * List<CollectorDto> CollectorDtoList = db.findAllByWhere(
	 * CollectorDto.class, " isDownLoad ==2 ", " date desc"); // 把第一个等待缓存的改为正在下载
	 * if (CollectorDtoList.size() != 0) { CollectorDto firstItem =
	 * CollectorDtoList.get(0); firstItem.setIsDownLoad(0);
	 * db.update(firstItem); // 打开service下载下一个等待缓存的 Intent downLoadIntent = new
	 * Intent(getApplicationContext(), DownLoadService.class);
	 * downLoadIntent.putExtra("_id", CollectorDtoList.get(0) .get_id());
	 * downLoadIntent.putExtra("title", CollectorDtoList.get(0) .getTitle());
	 * startService(downLoadIntent); } } }
	 * 
	 * }
	 */
}
