package net.tatans.coeus.novel.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.services.DownLoadService;
import net.tatans.coeus.novel.tools.InputMethodlUtil;
import net.tatans.coeus.speech.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.Date;

public class MoreActivity extends BaseActivity implements OnClickListener {
    private String title;
    private int currentPosition, isDownLoad;
    private EditText input_chapter;
    private LinearLayout ll_goto, ll_cache, ll_catalog, ll_replace_resource;
    private int requestCode = 1;
    private int totalChapterCount;
    private String bookId;
    public TatansDb db;
    private String source = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_activity);
        db = TatansDb.create(AppConstants.TATANS_DB_NAME);
        setTitle("更多");
        initView();
        initdata();
        source = db.findById(bookId, CollectorDto.class)
                .getSource();
        // new myAsycTesk().execute();
    }

    @SuppressLint("NewApi")
    private void initView() {

        ll_catalog = (LinearLayout) findViewById(R.id.ll_catalog);
        ll_catalog.setOnClickListener(this);
        ll_cache = (LinearLayout) findViewById(R.id.ll_cache);
        ll_cache.setOnClickListener(this);
        ll_replace_resource = (LinearLayout) findViewById(R.id.ll_replace_resource);
        ll_replace_resource.setOnClickListener(this);
        ll_goto = (LinearLayout) findViewById(R.id.ll_goto);
        ll_goto.setOnClickListener(this);
        input_chapter = (EditText) findViewById(R.id.input_chapter);
        input_chapter.setOnHoverListener(new OnHoverListener() {

            @Override
            public boolean onHover(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_EXIT:
                        InputMethodlUtil.toggleInputMethod(MoreActivity.this, view);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void initdata() {
        Intent intent = getIntent();
        totalChapterCount = intent.getIntExtra("totalChapterCount", 0);
        bookId = intent.getStringExtra("bookId");
        isDownLoad = intent.getIntExtra("isDownLoad", -1);
        currentPosition = intent.getIntExtra("currentPosition", 0);
        title = intent.getStringExtra("title");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_goto:
                String chapterText = input_chapter.getText().toString();
                int chapter = 0;
                if (!chapterText.equals("")) {
                    chapter = Integer.parseInt(chapterText);
                }

                if (chapter > 0 && chapter <= totalChapterCount) {
                    Intent intent = new Intent();
                    intent.setClass(MoreActivity.this, ContentActivity.class);
                    intent.putExtra("bookId", bookId);
                    intent.putExtra("totalChapterCount", totalChapterCount);
                    intent.putExtra("isDownLoad", isDownLoad);
                    intent.putExtra("currentPosition", chapter - 1);
                    intent.putExtra("title", title);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (totalChapterCount == 0) {
                    showToast("未能请求到章节数据");
                } else {
                    showToast("请输入1到" + totalChapterCount + "之间的数字");
                }

                break;
            case R.id.ll_catalog:

                Intent intentCatalog = new Intent();
                intentCatalog
                        .setClass(MoreActivity.this, ChapterListActivity.class);
                intentCatalog.putExtra("bookId", bookId);
                intentCatalog.putExtra("totalChapterCount", totalChapterCount);
                intentCatalog.putExtra("isDownLoad", isDownLoad);
                intentCatalog.putExtra("title", title);
                intentCatalog.putExtra("currentPosition", currentPosition);
                startActivityForResult(intentCatalog, requestCode);
                break;
            case R.id.ll_cache:
                // 打开service下载下一个等待缓存的
                download();
                break;
            case R.id.ll_replace_resource:
                if (isDownLoad != 3) {
                    Intent intentReplace = new Intent();
                    intentReplace.setClass(MoreActivity.this,
                            ReplaceResourceActivity.class);
                    intentReplace.putExtra("bookId", bookId);
                    intentReplace.putExtra("totalChapterCount", totalChapterCount);
                    intentReplace.putExtra("isDownLoad", isDownLoad);
                    intentReplace.putExtra("title", title);
                    intentReplace.putExtra("currentPosition", currentPosition);
                    startActivityForResult(intentReplace, requestCode);
                } else {
                    showToast("本地小说不支持换源");
                }

                break;

            default:
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodlUtil.hideInputMethod(
                MoreActivity.this, input_chapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodlUtil.hideInputMethod(
                MoreActivity.this, input_chapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int isDownLoad = data.getIntExtra("isDownLoad", -1);
                int currentPosition = data.getIntExtra("currentPosition", 0);
                totalChapterCount = data.getIntExtra("totalChapterCount", 0);
                int sourceNum = data.getIntExtra("sourceNum", 0);
                bookId = data.getStringExtra("bookId");
                String title = data.getStringExtra("title");
                Intent intent = new Intent();
                intent.setClass(MoreActivity.this, ContentActivity.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("totalChapterCount", totalChapterCount);
                intent.putExtra("sourceNum", sourceNum);
                intent.putExtra("isDownLoad", isDownLoad);
                intent.putExtra("currentPosition", currentPosition);
                intent.putExtra("title", title);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void showToast(String text) {
        TatansToast.showAndCancel(text);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TatansToast.cancel();
    }

    private void download() {
        if (!NetWorkUtil.hasNetworkConnection(MoreActivity.this)) {
            showToast("当前网络未连接，请检查网络");
            return;
        }
        int isDownLoad;
        CollectorDto collerctor = db.findById(bookId, CollectorDto.class);
        if (collerctor == null) {
            isDownLoad = -1;
        } else {
            isDownLoad = db.findById(bookId, CollectorDto.class)
                    .getIsDownLoad();
        }
        if (isDownLoad == 3) {
            showToast("本地小说无法下载");
            return;
        }

        if (isWorked(getApplicationContext())) {
            if (collerctor != null && isDownLoad == 0) {
                showToast("该小说正在下载中,勿重复操作");
                return;
            } else if (collerctor != null && isDownLoad == 2) {
                showToast("该小说已经在缓存队列中,勿重复操作");
                return;
            } else {
                showToast("已加入缓存队列");
                CollectorDto collector = new CollectorDto(bookId, title, 0, 2,
                        new Date(), totalChapterCount, 0, -1, 0, source);
                db.update(collector);
            }
            return;
        }
        Intent downLoadIntent = new Intent(MoreActivity.this,
                DownLoadService.class);
        downLoadIntent.putExtra("bookId", bookId);
        downLoadIntent.putExtra("title", title);
        downLoadIntent.putExtra("source", source);
        Log.d("OOOOOOOO", source);
        // Boolean flag = true;
        // // 判断是否已经下载过
        // List<CollectorDto> MyList = db.findAllByWhere(CollectorDto.class,
        // "isDownLoad = 1");
        // if (MyList.size() != 0) {
        // for (int i = 0; i < MyList.size(); i++) {
        // if (MyList.get(i).get_id().equals(sourceId)) {
        // showToast("您已经缓存过该小说");
        // flag = false;
        // break;
        // }
        // }
        // }
        // if (flag) {
        // 加入到书藏书籍数据库
        CollectorDto collector = new CollectorDto(bookId, title, 0, 0,
                new Date(), totalChapterCount, 0, -1, 0, source);
        if (collerctor == null) {
            db.save(collector);
        } else {
            db.update(collector);
        }
        showToast("准备缓存");
        // 进度条显示
        startService(downLoadIntent);
        // }
    }

    // 判断下载service是否已在工作
    public static boolean isWorked(Context context) {
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
                .getRunningServices(45);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals("net.tatans.coeus.novel.DownLoadService")) {
                return true;
            }
        }
        return false;
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
