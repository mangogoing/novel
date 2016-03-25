package net.tatans.coeus.novel.activities;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.BookBriefDto;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.services.DownLoadService;
import net.tatans.coeus.novel.tools.NetworkUtils;
import net.tatans.coeus.novel.tools.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookBriefActivity extends BaseActivity implements
        OnItemClickListener, OnClickListener {
    private String title;
    public TatansDb db;
    private List<BookBriefDto> ClassificatList = new ArrayList<BookBriefDto>();
    private TextView tv_loading, tv_introduct, tv_collect, tv_read,
            tv_downLoad, tv_progress;
    private MyDownLoadProgressReciver progressReciver;
    private String source = "0";
    private int totalChapterCount;
    private String bookId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = TatansDb.create(AppConstants.TATANS_DB_NAME);
        setContentView(R.layout.introduct_activity);
        title = getIntent().getStringExtra("title");
        bookId = getIntent().getStringExtra("_id");
        initView();
        setTitle("书籍" + title);
        registerMyReceiver();
        new myAsycTesk().execute();
        // new chapterAsycTesk().execute();
    }


    private void registerMyReceiver() {
        if (progressReciver == null) {
            progressReciver = new MyDownLoadProgressReciver();
        }
        IntentFilter filter = new IntentFilter(UrlUtil.ACTION);
        this.registerReceiver(progressReciver, filter);
    }

    private void initView() {
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        tv_introduct = (TextView) findViewById(R.id.tv_introduct);
        tv_introduct.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_collect = (TextView) findViewById(R.id.tv_collect);
        // tv_catalog = (TextView) findViewById(R.id.tv_catalog);
        tv_read = (TextView) findViewById(R.id.tv_read);
        tv_downLoad = (TextView) findViewById(R.id.tv_downLoad);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_collect.setOnClickListener(this);
        // tv_catalog.setOnClickListener(this);
        tv_read.setOnClickListener(this);
        tv_downLoad.setOnClickListener(this);

    }

	/*
     * private String l(String paramString) { Object localObject = null; try {
	 * String str1 = "/chapter/" + URLEncoder.encode(paramString, "UTF8");
	 * String str2 = CipherUtil.a(str1); // String str3 =
	 * this.b.getString(2131099957); Object[] arrayOfObject = new Object[2];
	 * arrayOfObject[0] = str1; arrayOfObject[1] = str2; // String str4 =
	 * String.format(str3, arrayOfObject); // localObject = str4; return
	 * (String) localObject; } catch (UnsupportedEncodingException
	 * localUnsupportedEncodingException) { while (true)
	 * localUnsupportedEncodingException.printStackTrace(); } }
	 */

    class myAsycTesk extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String bookBriefUrl = UrlUtil.BOOK + bookId;
            TatansHttp http = new TatansHttp();
            http.addHeader("User-Agent",
                    "ZhuiShuShenQi/3.30.2(Android 5.1.1; TCL TCL P590L / TCL TCL P590L; )");
            http.get(bookBriefUrl, new HttpRequestCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    super.onSuccess(result);
                    AnalyzeResult(result);
                }
            });

            return null;

        }
    }

    private void AnalyzeResult(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String text = "书名：" + json.getString("title") + "。\n" + "作者："
                    + json.getString("author") + "。\n" + "分类："
                    + json.getString("cat") + "。\n" + "最新章节："
                    + json.getString("lastChapter") + "。\n" + "简介："
                    + json.getString("longIntro") + "。\n";
            tv_introduct.setText(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv_loading.setVisibility(View.GONE);
    }

	/*
     * private void json2Gson(String result) { try { JSONObject json = new
	 * JSONObject(result.toString()); String ok = json.getString("ok"); String
	 * allSources = ""; String updateTime = ""; if (ok.equals("true")) {
	 * JSONArray arraySources = json.getJSONArray("sources"); int lenght =
	 * arraySources.length(); SourcesDto sourcesDto = null;
	 * 
	 * for (int i = 0; i < lenght; i++) { String _id =
	 * arraySources.getJSONObject(i).getString("_id"); String source =
	 * arraySources.getJSONObject(i).getString( "source"); String source1 = "资源"
	 * + (i + 1) + ":" + source + "\n"; allSources = allSources + source1;
	 * String sourceId = arraySources.getJSONObject(i).getString( "sourceId");
	 * String book = arraySources.getJSONObject(i).getString( "book"); String
	 * priority = arraySources.getJSONObject(i).getString( "priority"); int
	 * chapterCount = arraySources.getJSONObject(i).getInt( "chapterCount");
	 * String lastChapter = arraySources.getJSONObject(i)
	 * .getString("lastChapter"); String updated =
	 * arraySources.getJSONObject(i).getString( "updated"); updateTime =
	 * updated; sourcesDto = new SourcesDto(_id, source, sourceId, book,
	 * priority, chapterCount, lastChapter, updated);
	 * sourcesList.add(sourcesDto); }
	 * 
	 * } totalChapterCount = NovelUtil.getChapterCount(sourcesList); sourceId =
	 * NovelUtil.getSourceId(sourcesList); source =
	 * NovelUtil.getSource(sourcesList); if (sourceId.equals("")) {
	 * showToast("未能请求到数据"); finish(); }
	 * 
	 * // tv_introduct.setText("书名：" + title); // tv_introduct.setText("书名：" +
	 * title + "。\n" + "更新时间：" + updateTime // + "。\n" + allSources); } catch
	 * (JSONException e) { e.printStackTrace(); }
	 * 
	 * }
	 */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Log.d("lion", ClassificatList.get(position).get_id());

    }

    @Override
    public void onClick(View v) {
        int isDownLoad;
        CollectorDto collerctor = db.findById(bookId, CollectorDto.class);
        if (collerctor == null) {
            isDownLoad = -2;
        } else {
            isDownLoad = db.findById(bookId, CollectorDto.class)
                    .getIsDownLoad();
        }
        switch (v.getId()) {
            /**
             * 收藏小说储存到数据库
             */
            case R.id.tv_collect:
                Boolean b = true;
                List<CollectorDto> list = db.findAll(CollectorDto.class);
                if (list.size() != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).get_id().equals(bookId)) {
                            showToast("您已经收藏过该小说");
                            tv_collect.setText("已收藏");
                            tv_collect.setEnabled(false);
                            b = false;
                            break;
                        }
                    }
                }
                if (b) {
                    CollectorDto collector = new CollectorDto(bookId, title, 0, -1,
                            new Date(), totalChapterCount, 0, -1, 0, source);
                    db.save(collector);
                    showToast("收藏成功");
                    tv_collect.setText("已收藏");
                }
                list = db.findAll(CollectorDto.class, " date desc");
                break;
            // case R.id.tv_catalog:
            // Intent intentCatalog = new Intent();
            // intentCatalog.setClass(BookBriefActivity.this,
            // ChapterListActivity.class);
            // intentCatalog.putExtra("isDownLoad", isDownLoad);
            // intentCatalog.putExtra("source", source);
            // intentCatalog.putExtra("totalChapterCount", totalChapterCount);
            // intentCatalog.putExtra("title", title);
            // intentCatalog.putExtra("bookId", bookId);
            // intentCatalog.putExtra("BookBriefActivity", true);
            //
            // startActivity(intentCatalog);
            // break;
            case R.id.tv_read:
                Intent intentRead = new Intent(BookBriefActivity.this,
                        ContentActivity.class);
                CollectorDto item = db.findById(bookId, CollectorDto.class);
                if (item != null) {
                    intentRead.putExtra("isDownLoad", item.getIsDownLoad());
                    intentRead.putExtra("currentPosition", item.getSavedPosition());
                    intentRead.putExtra("position", item.getPosition());
                    intentRead.putExtra("sentenceIndex", item.getSentenceIndex());
                    intentRead.putExtra("countPage", item.getCountPage());
                    intentRead.putExtra("isCollector", true);
                } else {
                    intentRead.putExtra("isDownLoad", isDownLoad);
                    intentRead.putExtra("currentPosition", 0);
                }
                intentRead.putExtra("bookId", bookId);
                intentRead.putExtra("source", source);
                intentRead.putExtra("totalChapterCount", totalChapterCount);
                intentRead.putExtra("title", title);
                startActivity(intentRead);
                break;
            case R.id.tv_downLoad:
                if (isWorked(getApplicationContext())) {
                    if (collerctor != null && collerctor.getIsDownLoad() == 0) {
                        showToast("该小说正在下载中,勿重复操作");
                        return;
                    } else if (collerctor != null
                            && collerctor.getIsDownLoad() == 1) {
                        showToast("该小说已缓存完成,勿重复操作");
                        return;
                    } else if (collerctor != null
                            && collerctor.getIsDownLoad() == 2) {
                        showToast("该小说已经在缓存队列中,勿重复操作");
                        return;
                    } else {
                        showToast("已加入缓存队列");
                        CollectorDto collector = new CollectorDto(bookId, title, 0,
                                2, new Date(), totalChapterCount, 0, -1, 0, source);
                        if (collerctor != null && isDownLoad == -1) {
                            db.update(collector);
                        } else {
                            db.save(collector);
                        }
                    }
                    return;
                }
                final Intent downLoadIntent = new Intent(BookBriefActivity.this,
                        DownLoadService.class);
                downLoadIntent.putExtra("bookId", bookId);
                downLoadIntent.putExtra("title", title);
                downLoadIntent.putExtra("source", source);
                Boolean flag = true;
                // 判断是否已经下载过
                List<CollectorDto> MyList = db.findAllByWhere(CollectorDto.class,
                        "isDownLoad = 1");
                if (MyList.size() != 0) {
                    for (int i = 0; i < MyList.size(); i++) {
                        if (MyList.get(i).get_id().equals(bookId)) {
                            showToast("您已经缓存过该小说");
                            tv_downLoad.setText("已缓存");
                            tv_downLoad.setEnabled(false);
                            tv_downLoad.setContentDescription("缓存完成");
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    // 加入到书藏书籍数据库
                    CollectorDto collector = new CollectorDto(bookId, title, 0, 0,
                            new Date(), totalChapterCount, 0, -1, 0, source);
                    if (collerctor == null) {
                        db.save(collector);
                    } else {
                        db.update(collector);
                    }
                    // 进度条显示
                    tv_progress.setVisibility(View.VISIBLE);
                    tv_downLoad.setEnabled(false);
                    startService(downLoadIntent);

                }
                break;
            default:
                break;
        }

    }

    private void showToast(String text) {
        TatansToast.showAndCancel(text);
    }

    // class chapterAsycTesk extends AsyncTask<Void, Void, String> {
    // @Override
    // protected String doInBackground(Void... params) {
    // TatansHttp http = new TatansHttp();
    // String url = UrlUtil.CHAPTER_LIST_URL + _id;
    // Log.d("ssssssss", url);
    // http.get(url, new HttpRequestCallBack<String>() {
    // @Override
    // public void onLoading(long count, long current) {
    // super.onLoading(count, current);
    // }
    //
    // @Override
    // public void onSuccess(String arg0) {
    // super.onSuccess(arg0);
    // jsonChapterGson(arg0);
    // }
    //
    // @Override
    // public void onFailure(Throwable t, String strMsg) {
    // showToast("未能请求到数据，请检查网络");
    // Log.d("NetWorkError", strMsg);
    // super.onFailure(t, strMsg);
    // }
    // });
    // return null;
    //
    // }
    //
    // }
    //
    // private void jsonChapterGson(String result) {
    // Log.d("ClassificatTwoActivity", result + "");
    // titleList.clear();
    // linkList.clear();
    // try {
    //
    // JSONObject json = new JSONObject(result.toString());
    // String ok = json.getString("ok");
    // if (ok.equals("true")) {
    // JSONObject jsonMixToc = new JSONObject(json.getString("mixToc"));
    // JSONArray chaptersArray = jsonMixToc.getJSONArray("chapters");
    // for (int i = 0; i < chaptersArray.length(); i++) {
    // String title = chaptersArray.getJSONObject(i).getString(
    // "title");
    // String link = chaptersArray.getJSONObject(i).getString(
    // "link");
    // linkList.add(link);
    // titleList.add(title);
    // }
    // }
    // } catch (JSONException e) {
    // e.printStackTrace();
    // }
    //
    // }

    // @Override
    // public void onResume() {
    // super.onResume();
    // setTitle("书籍" + title);
    // }

    @Override
    protected void onStop() {
        super.onStop();
        TatansToast.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(progressReciver);
    }

    // 接受下载service发出的广播刷新进度条
    private class MyDownLoadProgressReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Log.d("AAAA", "percent------");
            double percent = bundle.getDouble("percent");
            String brodcast_bookId = bundle.getString("bookId");
            if (brodcast_bookId.equals(bookId)) {
                tv_progress.setVisibility(View.VISIBLE);
                tv_progress.setText("已缓存" + percent + "%");
                if (percent == 100.0) {
                    if (!NetworkUtils
                            .isNetworkConnected(getApplicationContext())) {
                        tv_progress.setText("网络被断开啦");
                    } else {
                        tv_progress.setText("缓存成功" + "： " + title + " 已保存到sd卡");
                    }
                    Intent downLoadIntent = new Intent(BookBriefActivity.this,
                            DownLoadService.class);
                    stopService(downLoadIntent);
                }

            }
        }
    }

    // 判断下载service是否已在工作
    public static boolean isWorked(Context context) {
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
                .getRunningServices(45);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals("net.tatans.coeus.novel.services.DownLoadService")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();

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

}
