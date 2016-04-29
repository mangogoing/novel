package net.tatans.coeus.novel.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.constant.AppConstants;

public class PromptActivity extends BaseActivity implements OnClickListener {
    private String title;
    private int currentPosition, isDownLoad;
    private LinearLayout ll_prompt, ll_collect, ll_cancel;
    private int requestCode = 1;
    private int totalChapterCount;
    private String bookId;
    public TatansDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_activity);
        db = TatansDb.create(AppConstants.TATANS_DB_NAME);
        setTitle("收藏提示");
        initView();
        // initdata();
        // new myAsycTesk().execute();
    }

    @SuppressLint("NewApi")
    private void initView() {
        ll_cancel = (LinearLayout) findViewById(R.id.ll_cancel);
        ll_cancel.setOnClickListener(this);
        ll_collect = (LinearLayout) findViewById(R.id.ll_collect);
        ll_collect.setOnClickListener(this);
        // ll_prompt = (LinearLayout) findViewById(R.id.ll_prompt);
        // ll_prompt.setOnClickListener(this);
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
        Intent intent = new Intent();
        switch (v.getId()) {

            case R.id.ll_collect:

                intent.setClass(PromptActivity.this, NovelDisplayActivity.class);
                intent.putExtra("isCollect", true);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.ll_cancel:
                intent.setClass(PromptActivity.this, NovelDisplayActivity.class);
                intent.putExtra("isCollect", false);
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            intent.setClass(PromptActivity.this, NovelDisplayActivity.class);
            intent.putExtra("isCollect", false);
            setResult(RESULT_OK, intent);
            finish();
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        TatansToast.cancel();
//    }

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
