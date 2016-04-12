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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
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
import net.tatans.coeus.novel.services.DownLoadService;
import net.tatans.coeus.novel.tools.FilePathUtil;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.JsonUtils;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.speech.util.NetWorkUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReplaceResourceActivity extends BaseActivity implements
        OnItemClickListener {

    private ListView lv_one_list;
    private TitleAdapter listAdapter;
    private TextView tv_loading;
    private String title;
    private int isDownLoad;
    private int currentPage = 1;
    private List<String> titleList = new ArrayList<String>();
    private List<String> isDownloadList = new ArrayList<String>();
    private Handler handler = new Handler();
    private boolean isSpeak;
    private String bookId, sourceListFilePath;
    private int totalChapterCount;
    private int currentPosition;
    protected List<ChapterDto> ChapterList;
    private RequestQueue mRequestQueue;
    private int sourceNum;
    private TatansDb db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        db = TatansDb.create(AppConstants.TATANS_DB_NAME);
        lv_one_list = (ListView) findViewById(R.id.lv_main);
        tv_loading = (TextView) findViewById(R.id.tv_loading);
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
        mRequestQueue = Volley.newRequestQueue(ReplaceResourceActivity.this);
        Intent intent = getIntent();
        totalChapterCount = intent.getIntExtra("totalChapterCount", 0);
        bookId = intent.getStringExtra("bookId");
        isDownLoad = intent.getIntExtra("isDownLoad", -1);
        currentPosition = intent.getIntExtra("currentPosition", 0);
        title = intent.getStringExtra("title");
        setTitle(title + "资源列表");
        lv_one_list.setOnItemClickListener(this);
        String source;
        try {
            source = db.findById(bookId, CollectorDto.class)
                    .getSource();
        } catch (NullPointerException e) {
            source = "0";
        }
        sourceNum = Integer.parseInt(source);
        init();

    }

    private void init() {
        sourceListFilePath = FilePathUtil.getFilePath(bookId, UrlUtil.SOURCE_LIST_TXT, 0);
        if (isDownLoad == 1 && FileUtil.fileIsExists(sourceListFilePath)
                || isDownLoad == 3 && FileUtil.fileIsExists(sourceListFilePath)
                || isDownLoad == 0 && FileUtil.fileIsExists(sourceListFilePath)) {
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
                String result = FileUtil.read(sourceListFilePath).toString();
                summarylist = JsonUtils.getSummaryListByJson(result);
                String chapterFilePath;
                for (int i = 0; i < summarylist.size(); i++) {
                    chapterFilePath = FilePathUtil.getFilePath(bookId, -1, i);
                    if (FileUtil.fileIsExists(chapterFilePath)) {
                        isDownloadList.add("已下载");
                    } else {
                        isDownloadList.add("未下载");
                    }
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

        listAdapter = new TitleAdapter(getApplicationContext(), titleList);
        lv_one_list.setAdapter(listAdapter);
        lv_one_list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (!NetWorkUtil.hasNetworkConnection(ReplaceResourceActivity.this)) {
            TatansToast.showAndCancel("当前网络未连接，请检查网络。");
            return;
        }
        CollectorDto collerctor = db.findById(bookId, CollectorDto.class);

        int isDownLoad;
        if (collerctor == null) {
            isDownLoad = -2;
        } else {
            isDownLoad = db.findById(bookId, CollectorDto.class)
                    .getIsDownLoad();
        }
        // 加入到书藏书籍数据库
        if (sourceNum != position) {
            // 下载完成停止该service
            Intent finishIntent = new Intent(UrlUtil.FINISH_ACTION);
            finishIntent.putExtra("bookId", bookId);
            finishIntent.putExtra("replace", true);
            finishIntent.putExtra("isLoading", isLoading(collerctor));
            sendBroadcast(finishIntent);
            CollectorDto collector = new CollectorDto(bookId, title, 0, -1,
                    new Date(), totalChapterCount, 0, -1, 0, position + "");
            if (collerctor == null) {
                db.save(collector);
            } else {
                db.update(collector);
            }

//            String filePath = Environment.getExternalStorageDirectory()
//                    + "/tatans/novel/" + bookId;
//            final File file = new File(filePath);
//            new Thread(new Runnable() {
//                public void run() {
//                    FileUtil.delete(file);
//                }
//            }).start();

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
        } else {
            showToast("当前资源为已选中资源，请选择其他资源换源。");
        }


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

    private boolean isLoading(CollectorDto collerctor) {
        Log.e("TTTTTTTT", "--------" + BookBriefActivity.isWorked(getApplicationContext()));
        if (BookBriefActivity.isWorked(getApplicationContext())) {
            if (collerctor != null && collerctor.getIsDownLoad() == 0) {
                return true;
            }
            return false;
        }
        return false;
    }

}
