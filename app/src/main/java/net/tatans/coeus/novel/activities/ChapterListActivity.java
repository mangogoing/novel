package net.tatans.coeus.novel.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.util.HttpProces;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.TitleAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.ChapterDto;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.dto.SummaryDto;
import net.tatans.coeus.novel.tools.FilePathUtil;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.JsonUtils;
import net.tatans.coeus.novel.tools.SharedPreferencesUtil;
import net.tatans.coeus.novel.tools.UrlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChapterListActivity extends BaseActivity implements
        OnItemClickListener {

    private ListView lv_one_list;
    private TextView tv_loading;
    private TitleAdapter listAdapter;
    private String title, chapterFilePath, sourceFilePath;
    private int isDownLoad, currentPosition;
    private List<String> titleList = new ArrayList<String>();
    private List<Integer> sortList = new ArrayList<Integer>();
    Handler handler = new Handler();
    private boolean isBookBriefActivity;
    private TatansDb db;
    private String bookId;
    private int totalChapterCount;
    protected List<ChapterDto> ChapterList;
    private RequestQueue mRequestQueue;
    private int sourceNum = 0;
    private boolean isFirstTouch = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = TatansDb.create("MyCollector");
        setContentView(R.layout.list);
        lv_one_list = (ListView) findViewById(R.id.lv_main);
        lv_one_list.setOnItemClickListener(this);
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        tv_loading.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        if (!isFirstTouch) {
                            showToast(getString(R.string.loading_hint));
                        }
                        isFirstTouch = false;
                        break;
                }
                return false;
            }
        });
        mRequestQueue = Volley.newRequestQueue(ChapterListActivity.this);
        Intent intent = getIntent();
        totalChapterCount = intent.getIntExtra("totalChapterCount", 0);
        bookId = intent.getStringExtra("bookId");
        isDownLoad = intent.getIntExtra("isDownLoad", -1);
        isBookBriefActivity = intent
                .getBooleanExtra("BookBriefActivity", false);
        currentPosition = intent.getIntExtra("currentPosition", 0);
        title = intent.getStringExtra("title");
        setTitle(title + "章节列表");
        String source;
        try {
            source = db.findById(bookId, CollectorDto.class)
                    .getSource();
        } catch (NullPointerException e) {
            source = "0";
        }
        sourceNum = Integer.parseInt(source);

//        init();
        new readSummaryListFromSDcard().execute();

    }

//    private void init() {
//        sourceFilePath = FilePathUtil.getFilePath(bookId, UrlUtil.SOURCE_LIST_TXT, 0);
//        if (FileUtil.fileIsExists(sourceFilePath)) {
//            // 如果资源列表txt存在读取资源列表
//            new readSummaryListFromSDcard().execute();
//        } else {
//            // 网络请求
//            getSummaryResource(this, mRequestQueue, bookId);
//        }
//
//    }

    class readSummaryListFromSDcard extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if (isDownLoad == 3) {
                json2Gson("");
                return null;
            }
            chapterFilePath = FilePathUtil.getFilePath(bookId, UrlUtil.CHAPTER_LIST_TXT, sourceNum);
            if (FileUtil.fileIsExists(chapterFilePath)) {
                // 如果章节列表txt存在读取章节列表
                String mResult = "";
                try {
                    mResult = FileUtil.read(chapterFilePath).toString();
                    json2Gson(mResult);
                } catch (IOException e) {
                    getSummaryResource(ChapterListActivity.this, mRequestQueue, bookId);
                }
            } else {
                // 网络请求
                getSummaryResource(ChapterListActivity.this, mRequestQueue, bookId);

            }
            return null;
        }
    }

    List<SummaryDto> summarylist;

    /**
     * @param context
     * @param bookId
     * @return 网站资源列表
     */
    private void getSummaryResource(Context context,
                                    final RequestQueue mRequestQueue, String bookId) {
        String bookBriefUrl = UrlUtil.RESOURCE_LIST + bookId;
        TatansHttp http = new TatansHttp();
        http.addHeader("User-Agent",
                "ZhuiShuShenQi/3.30.2(Android 5.1.1; TCL TCL P590L / TCL TCL P590L; )");
        http.get(bookBriefUrl, new HttpRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                summarylist = JsonUtils.getSummaryListByJson(result.toString());
                if (summarylist.size() > 0) {
                    if (sourceNum > summarylist.size() - 1) {
                        sourceNum = summarylist.size() - 1;
                    }
                    totalChapterCount = summarylist.get(sourceNum)
                            .getChaptersCount();
                    getChapterResource(summarylist.get(sourceNum).get_id());
                }
            }

            @Override
            public void onFailure(Throwable t, String strMsg) {
                HttpProces.failHttp();
                showToast("未能获取到资源列表，请检查网络");
                finish();
            }
        });

    }

    /**
     * @param
     * @param newBookId
     * @return 网站资源列表
     */
    private void getChapterResource(final String newBookId) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                InputStream urlStream = null;
                BufferedReader reader = null;
                String result = "";
                try {
                    HttpURLConnection cumtConnection = (HttpURLConnection) new URL(
                            UrlUtil.RESOURCE_BOOK_ID + newBookId
                                    + "?view=chapters").openConnection();
                    cumtConnection.setRequestProperty("User-Agent",
                            "YouShaQi/2.23.2 (iPhone; iOS 9.2; Scale/2.00)");
                    urlStream = cumtConnection.getInputStream();
                    reader = new BufferedReader(
                            new InputStreamReader(urlStream, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result = result + line;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                        urlStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                titleList = JsonUtils.getChapterNameListByJson(result);
                for (int i = 0; i < titleList.size(); i++) {
                    sortList.add(i + 1);
                }
                handler.post(result2json);
            }

        }).start();

    }

    private void json2Gson(String result) {
        Log.d("ClassificatTwoActivity", result + "");
        titleList.clear();
        sortList.clear();
        if (isDownLoad == 3) {
            try {
                titleList = FileUtil.read2Chapter(chapterFilePath);
                totalChapterCount = titleList.size();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < totalChapterCount; i++) {
                sortList.add(i + 1);
            }
            handler.post(result2json);
        } else {
            ChapterList = JsonUtils.getChapterListByJson(result);
            totalChapterCount = ChapterList.size();
            for (int i = 0; i < ChapterList.size(); i++) {
                titleList.add(ChapterList.get(i).getTitle());
                sortList.add(i + 1);
            }
            handler.post(result2json);
        }

    }

    Runnable result2json = new Runnable() {

        @Override
        public void run() {
            setListData();
        }
    };

    public void setListData() {
        if (titleList.size() == 0) {
            showToast("未能获取到列表，请检查网络，或者在更多选项中选择其他资源试试吧");
            finish();
            return;
        }
        listAdapter = new TitleAdapter(getApplicationContext(), titleList);
        lv_one_list.setAdapter(listAdapter);
        lv_one_list.setSelection(currentPosition);
        lv_one_list.setVisibility(View.VISIBLE);
        tv_loading.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // int currentPosition = (currentPage - 1)
        // * (int) AppConstants.APP_PAGE_SIZE + position;

        Intent intent = new Intent();
        intent.putExtra("bookId", bookId);
        intent.putExtra("totalChapterCount", totalChapterCount);
        intent.putExtra("isDownLoad", isDownLoad);
        intent.putExtra("currentPosition", position);
        intent.putExtra("title", title);
        if (isBookBriefActivity) {
            intent.setClass(ChapterListActivity.this,
                    NovelDisplayActivity.class);
            startActivity(intent);
        } else {
            intent.setClass(ChapterListActivity.this, MoreActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

	/*
     * @Override public void up() { if (pageCount > 1) {
	 * TatansToast.showAndCancel(this, "双指左右滑动可翻页"); } }
	 *
	 * @Override public void left() { currentPage++; if (currentPage >
	 * pageCount) { currentPage = pageCount; showToast("没有下一页了"); } else {
	 * showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
	 * setListData();
	 *
	 * }
	 *
	 * }
	 *
	 * @Override public void right() { currentPage--; if (currentPage < 1) {
	 * currentPage = 1; showToast("没有上一页了");
	 *
	 * } else { showToast("当前所在第" + currentPage + "页，共" + pageCount + "页");
	 * setListData();
	 *
	 * } }
	 *
	 * @Override public void down() { if (pageCount > 1) {
	 * TatansToast.showAndCancel(this, "双指左右滑动可翻页"); } }
	 */

    // private void speechShow(String text) {
    // if (speaker == null) {
    // speaker = Speaker.getInstance(ChapterListActivity.this);
    // }
    // speaker.speech(text);
    // }

    private void showToast(String text) {
        TatansToast.showAndCancel(text);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        TatansToast.cancel();
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
