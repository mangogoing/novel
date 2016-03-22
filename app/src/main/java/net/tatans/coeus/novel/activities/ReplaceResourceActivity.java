package net.tatans.coeus.novel.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.util.HttpProces;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.ChapterDto;
import net.tatans.coeus.novel.dto.SummaryDto;
import net.tatans.coeus.novel.tools.FilePathUtil;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.JsonUtils;
import net.tatans.coeus.novel.tools.SharedPreferencesUtil;
import net.tatans.coeus.novel.tools.UrlUtil;

import java.io.IOException;
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
    private String bookId, chapterFilePath;
    private int totalChapterCount;
    private int currentPosition;
    protected List<ChapterDto> ChapterList;
    private RequestQueue mRequestQueue;
    private int sourceNum;

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
        sourceNum = SharedPreferencesUtil
                .readData(ReplaceResourceActivity.this);
        init();

    }

    private void init() {
        chapterFilePath = FilePathUtil.getFilePath(bookId, UrlUtil.SOURCE_LIST_TXT, 0);
        if (isDownLoad == 1 && FileUtil.fileIsExists(chapterFilePath)
                || isDownLoad == 3 && FileUtil.fileIsExists(chapterFilePath)
                || isDownLoad == 0 && FileUtil.fileIsExists(chapterFilePath)) {
            // 如果章节列表txt存在读取章节列表
            new readFromSDcard().execute();

        } else {
            // 网络请求
            getSummaryResource(this, mRequestQueue, bookId);
        }

    }

    class readFromSDcard extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                String result = FileUtil.read(chapterFilePath).toString();
                summarylist = JsonUtils.getSummaryListByJson(result);
                for (int i = 0; i < summarylist.size(); i++) {
                    titleList.add(summarylist.get(i).getName());
                }
                if (summarylist.size() > 0) {
                    if (sourceNum > summarylist.size() - 1) {
                        sourceNum = summarylist.size() - 1;
                    }
//
                    totalChapterCount = summarylist.get(sourceNum)
                            .getChaptersCount();

                    handler.post(result2json);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                for (int i = 0; i < summarylist.size(); i++) {
                    titleList.add(summarylist.get(i).getName());
                }
                if (summarylist.size() > 0) {
                    if (sourceNum > summarylist.size() - 1) {
                        sourceNum = summarylist.size() - 1;
                    }
                    totalChapterCount = summarylist.get(sourceNum)
                            .getChaptersCount();
                    handler.post(result2json);
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

    Runnable result2json = new Runnable() {

        @Override
        public void run() {
            setListData();
        }
    };

    public void setListData() {
        if (titleList.size() == 0) {
            showToast("未能获取到资源列表，请检查网络");
            finish();
            return;
        }
        showToast("当前资源" + summarylist.get(sourceNum).getName());
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
        TatansToast.showAndCancel(text);
    }

}
