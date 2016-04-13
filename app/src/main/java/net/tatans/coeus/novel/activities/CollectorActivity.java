package net.tatans.coeus.novel.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.CollectorAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.services.DownLoadService;
import net.tatans.coeus.novel.tools.NetworkUtils;
import net.tatans.coeus.novel.tools.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的书架界面
 *
 * @author shiyunfei
 */
public class CollectorActivity extends BaseActivity {
    List<CollectorDto> list = new ArrayList<CollectorDto>();
    // 章节更新map
    Map<String, Integer> map = new HashMap<String, Integer>();
    CollectorAdapter adapter;
    public static int pageCount;
    Handler handler = new Handler();
    private ListView lv_main;
    private int currentPage = 1, netChapterCount = -1;
    private boolean isSpeak, isFirst = true;
    private TatansDb db;
    private RequestQueue mRequestQueue;
    private DownLoadProgressReciver downLoadProgressReciver = null;
    private ProgressDialog proDia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        setTitle("我的书架");
        registerMyReceiver(this);
        proDia = new ProgressDialog(this);
        proDia.setTitle("移除中");
        proDia.setCancelable(false);

        mRequestQueue = Volley.newRequestQueue(this);
        lv_main = (ListView) findViewById(R.id.lv_main);
        TextView tv_loading = (TextView) findViewById(R.id.tv_loading);
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
    }

    private void init() {

        lv_main.setVisibility(View.GONE);
        db = TatansDb.create(
                AppConstants.TATANS_DB_NAME);
        list = db.findAll(CollectorDto.class, " date desc");
        for (int i = 0; i < list.size(); i++) {
            Log.d("RRRRRRRRRR", list.get(i).getTitle());
        }
        if (list.size() == 0) {
            handler.post(nullLog);
        } else {
            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                handler.post(flushUi);
            } else {
                handler.post(flushUi);
            }
        }
        downLoadNext();
    }

    /**
     * 联网后若有还在等待缓存的小说，则自动下载
     */
    private void downLoadNext() {

        if (NetworkUtils.isNetworkConnected(getApplicationContext())
                && !BookBriefActivity.isWorked(getApplicationContext())) {
            List<CollectorDto> CollectorDtoList = new ArrayList<>();
            List<CollectorDto> CollectorDtoList1 = db.findAllByWhere(
                    CollectorDto.class, " isDownLoad ==2 ", " date ");
            List<CollectorDto> CollectorDtoList2 = db.findAllByWhere(
                    CollectorDto.class, " isDownLoad ==0 ", " date ");
            CollectorDtoList.addAll(CollectorDtoList1);
            CollectorDtoList.addAll(CollectorDtoList2);
            // 把第一个等待缓存的改为正在下载
            if (CollectorDtoList.size() != 0) {
                CollectorDto firstItem = CollectorDtoList.get(0);
                firstItem.setIsDownLoad(0);
                db.update(firstItem);
                // 打开service下载下一个等待缓存的
                final Intent downLoadIntent = new Intent(
                        getApplicationContext(), DownLoadService.class);
                downLoadIntent.putExtra("bookId", CollectorDtoList.get(0)
                        .get_id());
                downLoadIntent.putExtra("title", CollectorDtoList.get(0)
                        .getTitle());
                downLoadIntent.putExtra("source", CollectorDtoList.get(0)
                        .getSource());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        getApplicationContext().startService(downLoadIntent);

                    }
                }, 200);

            }
        }
    }

    private void getChapterUpdate() {
        handler.postDelayed(flushUi, 50);
    }

    class myAsycTesk extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            TatansHttp http = new TatansHttp();
            http.addHeader("User-Agent",
                    "YouShaQi/2.23.2 (iPhone; iOS 9.2; Scale/2.00)");
            String ids = "";
            for (int i = 0; i < list.size(); i++) {
                final String bookId = list.get(i).get_id();
                if (i == list.size() - 1) {
                    ids = ids + bookId;
                } else {
                    ids = ids + bookId + ",";
                }

            }
            String url = "http://api.zhuishushenqi.com/book?view=updated&id="
                    + ids;
            http.get(url, new HttpRequestCallBack<String>() {
                @Override
                public void onLoading(long count, long current) {
                    super.onLoading(count, current);
                }

                @Override
                public void onSuccess(String result) {
                    super.onSuccess(result);
                    JSONArray array;
                    try {
                        array = new JSONArray(result);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = (JSONObject) array.get(i);
                            map.put(json.getString("_id"),
                                    json.getInt("chaptersCount"));
                        }
                        setData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Throwable t, String strMsg) {
                }
            });
            return null;

        }
    }

	/*
     * @Override public void left() { currentPage++; if (currentPage >
	 * pageCount) { currentPage = pageCount; showToast("没有下一页了"); } else { if
	 * (!isFirst) { showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
	 * } isFirst = false; setData(); } }
	 * 
	 * @Override public void right() { currentPage--; if (currentPage < 1) {
	 * currentPage = 1; showToast("没有上一页了"); } else { if (!isFirst) {
	 * showToast("当前所在第" + currentPage + "页，共" + pageCount + "页"); } isFirst =
	 * false; setData(); }
	 * 
	 * }
	 * 
	 * @Override public void up() { if (pageCount > 1) {
	 * TatansToast.showAndCancel(this, "双指左右滑动可翻页"); } }
	 * 
	 * @Override public void down() { if (pageCount > 1) {
	 * TatansToast.showAndCancel(this, "双指左右滑动可翻页"); } }
	 */

    private void setData() {
        if (list.size() == 0) {
            return;
        }

        adapter = new CollectorAdapter(list, CollectorActivity.this,
                AppConstants.APP_PAGE_SIZE, currentPage, map, proDia);
        lv_main.setAdapter(adapter);
        lv_main.setVisibility(View.VISIBLE);
        handler.post(new Runnable() {

            @Override
            public void run() {
                int start = lv_main.getFirstVisiblePosition();
                int end = lv_main.getLastVisiblePosition();
                Log.d("position", start + ":" + end + "");
            }
        });

    }

    Runnable flushUi = new Runnable() {

        @Override
        public void run() {
            pageCount = (int) Math.ceil(list.size()
                    / AppConstants.APP_PAGE_SIZE);
            // showToast("我的书架，当前所在第" + currentPage + "页，共" + pageCount + "页");
            setData();
        }
    };
    Runnable nullLog = new Runnable() {

        @Override
        public void run() {
            showToast("您没有收藏任何小说，去其他地方看看吧");
            setContentView(R.layout.null_log);
        }
    };

    private void showToast(String text) {
        TatansToast.showAndCancel(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if (isSpeak) {
        // setTitle("我的书架");
        // } else {
        // isSpeak = true;
        // }
        init();
    }

    // 接受下载service发出的广播刷新进度条
    private class DownLoadProgressReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UrlUtil.ACTION)) {
                Bundle bundle = intent.getExtras();
                final double percent = bundle.getDouble("percent");
                String bookId = bundle.getString("bookId");
                final int position = getPostionByID(bookId);
                if (position == -1
                        || !NetworkUtils
                        .isNetworkConnected(getApplicationContext())) {
                    return;
                } else {
                    updateSingleRow(position, percent);
                }
            }
        }
    }

    private void registerMyReceiver(Context context) {
        IntentFilter filter = new IntentFilter(UrlUtil.ACTION);
        if (downLoadProgressReciver == null) {
            downLoadProgressReciver = new DownLoadProgressReciver();
        }
        context.registerReceiver(downLoadProgressReciver, filter);
    }

    private void unRegisterMyReceiver(Context context) {
        if (downLoadProgressReciver != null) {
            context.unregisterReceiver(downLoadProgressReciver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proDia != null) {
            proDia.dismiss();
        }
        unRegisterMyReceiver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TatansToast.cancel();
    }

    // 得到需要刷新item的位置
    private int getPostionByID(String novel_id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get_id().equals(novel_id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * ListView单条更新
     *
     * @param postion
     * @param percent
     */
    public void updateSingleRow(final int postion, final double percent) {

        lv_main.post(new Runnable() {
            public void run() {
                int start = lv_main.getFirstVisiblePosition();
                int Last = lv_main.getLastVisiblePosition();
                if (postion >= start && postion <= Last) {
                    View view = lv_main.getChildAt(postion - start);
                    view.setTag(percent);
                    adapter.getView(postion, view, lv_main);
                }
            }
        });
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
