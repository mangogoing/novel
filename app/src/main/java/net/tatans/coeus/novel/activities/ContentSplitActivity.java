package net.tatans.coeus.novel.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import android.widget.Toast;

import net.tatans.coeus.audio.manager.AudioManagerUtil;
import net.tatans.coeus.audio.util.AudioManagerCallBack;
import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansSpeaker;
import net.tatans.coeus.network.tools.TatansSpeakerCallback;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.adapter.MyPagerAdapter;
import net.tatans.coeus.novel.base.BaseActivity;
import net.tatans.coeus.novel.tools.HomeWatcher;
import net.tatans.coeus.novel.tools.HomeWatcher.OnHomePressedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类为长文本播放界面 继承该类后， 将要朗读的文本用setContent(“要播放的内容”);方法 即可朗读文本
 * 该界面有暂停、继续播放、获取当前读到第几句
 *
 * @author luojianqin
 */
@SuppressLint("UseSparseArrays")
@SuppressWarnings("deprecation")
public class ContentSplitActivity extends BaseActivity {
    private static String TAG = "ContentSplitActivity";

    // private TBISTextView mEditText;
    private ViewPager vp;
    private MyPagerAdapter adapter;
    private ArrayList<View> viewContainer = new ArrayList<View>();
    protected TextView next, pre, pause_or_play;
    private String sResult;
    private String[] split;
    private boolean isSpeaking ;
    private Map<String, Integer> map = new HashMap<String, Integer>();
    private static Map<Integer, Integer> page = new HashMap<Integer, Integer>();
    // private int countPage = 0;// 用于保存上一页的position
    private static String NEXT = "下一页";
    private String PRE = "上一页";
    private int flag;// 用来标记是下一条还是下一页
    private HomeWatcher mHomeWatcher = null;
    // 音频焦点控制
//    protected AudioManagerUtil mAudioManagerUtil;
//    private AudioManagerCallBack mAudioManagerCallBack;
    private TextView more;
    public TextView load;
    protected int isDownLoad;
    protected String bookId;
    protected int currentPosition;
    protected String title;
    protected boolean isCollector;
    protected int position;
    protected int countPage;
    public static int sentenceIndex = -1;// 句子的下标
    private boolean isFirstTouch = true;
    private boolean isStopPlay;
    private boolean isFlip;

    public void setsResult(String sResult) {
        this.sResult = sResult;
    }

    public void setFlag(int position) {
        this.flag = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        initListener();
        initView();
//        initAudio();
        Intent intent = getIntent();
        isDownLoad = intent.getIntExtra("isDownLoad", -1);
        bookId = intent.getStringExtra("bookId");
        currentPosition = intent.getIntExtra("currentPosition", 0);
        title = intent.getStringExtra("title");
        isCollector = intent.getBooleanExtra("isCollector", false);
        countPage = intent.getIntExtra("countPage", 0);
        sentenceIndex = intent.getIntExtra("sentenceIndex", -1);
        position = intent.getIntExtra("position", 0);

    }

    @Override
    public void left() {
        leftHandler();
    }

    @Override
    public void right() {
        rightHandler();
    }

    @Override
    public void up() {
        if ("暂停".equals(pause_or_play.getText().toString())&&isSpeaking) {
            speakPause();
        } else if ("播放".equals(pause_or_play.getText().toString())){
            speakResume();
        }
    }

    @Override
    public void down() {

    }

    /**
     * 初始化控件
     */
    @SuppressLint("NewApi")
    private void initView() {
        setContentView(R.layout.txt_play);
        load = (TextView) findViewById(R.id.load);
        load.setOnHoverListener(new View.OnHoverListener() {
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
        next = (TextView) findViewById(R.id.next);
        pause_or_play = (TextView) findViewById(R.id.pause);
        pre = (TextView) findViewById(R.id.pre);
        more = (TextView) findViewById(R.id.more);
        vp = ((ViewPager) findViewById(R.id.viewpager));
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 13) {
                    TatansToast.show("正在加载中",
                            Toast.LENGTH_SHORT);
                } else {
                    Log.d(TAG, "下一条");
                    sentenceIndex = -1;
                    nextInformation();
                }
            }
        });

        pre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "上一条");
                sentenceIndex = -1;
                preInformation();
            }
        });
        pause_or_play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("暂停".equals(pause_or_play.getText().toString())&&isSpeaking) {
                    speakPause();
                } else if ("播放".equals(pause_or_play.getText().toString())){
                    speakResume();
                }
            }
        });

        more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMore();
            }
        });
    }

    private void initListener() {
        mHomeWatcher = new HomeWatcher(ContentSplitActivity.this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                isStopPlay = true;
                speakPause();

            }

            @Override
            public void onHomeLongPressed() {
                isStopPlay = true;
                speakPause();
            }
        });

        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); // 获取系统服务
        telManager.listen(new MobliePhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHomeWatcher != null) {
            mHomeWatcher.startWatch();
        }
    }

    private boolean isHourly;

    /**
     * 音频控制
     */
//    protected void initAudio() {
//        mAudioManagerCallBack = new AudioManagerCallBack() {
//            @Override
//            public void onFocusLossTransient() {
//                super.onFocusLossTransient();
//                Log.d("radio", "onFocusLossTransient");
//                isHourly = true;
//                TatansApplication.pause();
//
//            }
//
//            @Override
//            public void onFocusLossTransientDuck() {
//                super.onFocusLossTransientDuck();
//                Log.d("radio", "onFocusLossTransientDuck");
//                if (speaker != null) {
//                    speaker.pause();
//                }
//
//            }
//
//            @Override
//            public void onFocusLoss() {
//                super.onFocusLoss();
//                Log.d("radio", "onFocusLoss");
//                if (speaker != null) {
//                    speaker.pause();
//                }
//            }
//
//            @Override
//            public void onFocusGain() {
//                super.onFocusGain();
//                Log.d("radio", "onFocusGain");
//                if (isHourly) {
//                    isHourly = false;
//                    readNextSentence();
//                } else {
//                    if (speaker != null) {
//                        speaker.resume();
//                    }
//
//                }
//                isSpeaking = true;
//            }
//        };
//        mAudioManagerUtil = new AudioManagerUtil(getApplicationContext(),
//                mAudioManagerCallBack);
//    }
    private void initalizeViewContainer() {
        int count = 3;
        viewContainer.clear();
        for (int i = 0; i < count; i++) {
            viewContainer.add(getLayoutInflater().inflate(R.layout.data_view,
                    null));
        }
    }

    private OnPageChangeListener changeListenner = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            Log.e("mytag", "onPageSelected---->arg0:" + arg0);
            if (arg0 > 1) {
                leftHandler();
                Log.e("mytag", "下一页：arg0" + arg0);
            }
            if (arg0 < 1) {
                rightHandler();
                Log.e("mytag", "上一页：arg0" + arg0);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private void rightHandler() {
        Log.e("SentenceSplitActivity", "mDetector 向右:");
        isSpeaking = false;
        isStopPlay = false;
        isFlip = true;
        PrePage();
        TatansApplication.stop();

        if ("没有上一页了".equals(PRE)) {// 不会从头播报
            preInformation();
        } else if ("上一页".equals(PRE)) {// 从本页第二句开始播报
            TatansApplication.speech("上一页", new TatansSpeakerCallback() {
                @Override
                public void onCompleted() {
                    super.onCompleted();
                    String[] nextSplit = adapter.getText().toString()
                            .split("，|。|！|？|；");
                    Log.i(TAG, "nextSplit[1]:" + nextSplit[1]);
                    sentenceIndex = map.get(nextSplit[1]) - 1;
                    readNextSentence();
                }
            });
        }
    }

    private void leftHandler() {
        Log.e("SentenceSplitActivity", "mDetector 向左:");
        isSpeaking = false;
        isStopPlay = false;
        isFlip = true;
        nextPage();
        TatansApplication.stop();

        if (flag == 2) {// 下一页
            TatansApplication.speech("下一页", new TatansSpeakerCallback() {
                @Override
                public void onCompleted() {
                    super.onCompleted();
                    String[] nextSplit = adapter.reString().toString()
                            .split("，|。|！|？|；");
                    try {
                        Log.i(TAG, "nextSplit[1]:" + nextSplit[1]);
                        sentenceIndex = map.get(nextSplit[1]) - 1;
                    } catch (Exception e) {
                        nextInformation();
                        return;
                    }
                    readNextSentence();
                }
            });
        } else if (flag == 1) {// 下一条
            nextInformation();
        }
    }

//    Handler mHandler = new Handler();

    private void setData(final String data) {
        if (null != data && !"".equals(data)) {
            // mEditText.setText(str);
            handler.post(new Runnable() {

                @Override
                public void run() {
                    initalizeViewContainer();
                    adapter = new MyPagerAdapter(ContentSplitActivity.this,
                            viewContainer, data);
                    vp.setAdapter(adapter);
                    vp.setOnPageChangeListener(changeListenner);
                    vp.setCurrentItem(1);
                    pause_or_play.setText("暂停");
                    pause_or_play.setContentDescription("暂停。按钮");
                    load.setVisibility(View.GONE);
                }
            });

        }
    }

    /**
     * 分割阅读内容，以"，|。|；|？|！"来分句
     */
    public void setContent(String txt) {
        String str = txt;
        if (!txt.endsWith("。")) {
            str = txt + "。";
        }
        if (!str.endsWith("。")) {
            str = str + "。";
        }
        split = str.toString().split("，|。|！|？|；");
        for (int i = 0; i < split.length; i++) {
            map.put(split[i].toString(), i);
        }
        setsResult(str);
        // mEditText.setText(str);
        if (null != str && !"".equals(str)) {
            // mEditText.setText(str);
            handler.post(new Runnable() {

                @Override
                public void run() {
                    String string = sResult.substring(position);
                    initalizeViewContainer();
                    adapter = new MyPagerAdapter(ContentSplitActivity.this,
                            viewContainer, string);
                    vp.setAdapter(adapter);
                    vp.setOnPageChangeListener(changeListenner);
                    vp.setCurrentItem(1);
                    load.setVisibility(View.GONE);
                    pause_or_play.setText("暂停");
                    pause_or_play.setContentDescription("暂停。按钮");
                    readNextSentence();
//                    mAudioManagerUtil.requestAudioFocus();
                }
            });

        }

    }

    /**
     * 从文本指定位置开始载入一页
     */
    private void loadPage(int position) {
        String string = sResult.substring(position);
        // mEditText.setText(string);
        setData(string);

    }

    /**
     * 下一页
     */
    public void nextPage() {
        // position = position + mEditText.getCharNum();
        position = position + adapter.getCharNum();
        if (position >= sResult.length()) {
            flag = 1;// 没有下一页了
        } else {
            flag = 2;// 有下一页
            loadPage(position);
            // mEditText.resize();
            adapter.resize();
            countPage++;
            page.put(countPage, position);
        }
    }

    /**
     * 上一页
     */
    public void PrePage() {
        try {
            countPage--;
            if (countPage < 0) {
                Log.i(TAG, "没有上一页了");
                PRE = "没有上一页了";
                countPage = 0;
            } else {
                PRE = "上一页";
                if (countPage == 0) {
                    position = 0;
                } else {
                    position = page.get(countPage);
                }
                loadPage(position);
                // mEditText.resize();
                adapter.resize();
//                if (!"暂停".equals(pause_or_play.getText().toString())) {
//                    pause_or_play.setText("播放");
//                    pause_or_play.setContentDescription("播放。按钮");
//                } else {
//                    pause_or_play.setText("暂停");
//                    pause_or_play.setContentDescription("暂停。按钮");
//                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace() + "");
        }
    }

    /**
     * 读取下一句话
     */
    public void readNextSentence() {
        if (isStopPlay) {
            pause_or_play.setText("播放");
            pause_or_play.setContentDescription("播放。按钮");
            isStopPlay = false;
            return;
        }
        isStopPlay = false;
        if (sResult != null) {
            sentenceIndex++;
            int length = split.length;
            // 如果内容已读完
            if (sentenceIndex >= length) {
                sentenceIndex = length;
                sentenceIndex = -1;
                nextInformation();

            } else {
                for (int i = sentenceIndex; i < split.length; i++) {
                    if ("".equals(split[sentenceIndex])
                            || split[sentenceIndex].length() < 0) {
                        sentenceIndex++;
                    }
                }
                Log.d("OOOOOPPPPPP", split[1]);
                TatansApplication.speech(split[sentenceIndex], new TatansSpeakerCallback() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
//                        if (sentenceIndex >= split.length) {
//                            TatansApplication.speech("", null);
//                        } else {
                        readNextSentence();
                    }
                });
                isSpeaking = true;
                Log.d("PPPPPPPP", countPage + "");
                // 如果当前正在读的内容不再当前页，则跳到下一页
//                Log.d("QQQQQQQQ", adapter.reString().toString());
                String str = adapter.reString().toString();
                if (!str.equals("") && !str.contains(split[sentenceIndex])) {
//                    Log.d("QQQQQQQQ", "nextPage");
                    nextPage();
                }
            }

        } else {
            TatansApplication.speech("本章内容为空");
            Intent intent = new Intent();
            ContentSplitActivity.this.setResult(RESULT_OK, intent);
            ContentSplitActivity.this.finish();
        }
    }

    /**
     * 获取当前读到的第几句话
     */
    public int getSentenceIndex() {
        return sentenceIndex + 1;
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        TatansToast.cancel();
//    }

    /**
     * 上一章
     */
    public void preInformation() {
        Log.d(TAG, "上一章");
        speakPauseNoTips();
    }

    /**
     * 下一章
     */
    public void nextInformation() {
        Log.d(TAG, "下一章");
        speakPauseNoTips();
    }

    public void gotoMore() {
        speakPauseNoTips();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHomeWatcher != null) {
            mHomeWatcher.stopWatch();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mAudioManagerUtil.abandonAudioFocus();
    }

    /**
     * 打断talkback
     */
    public void interruptTalkback(Context context) {
        Log.e("hhh", "打断talkback");
        try {
            AccessibilityManager accessibilityManager = (AccessibilityManager) context
                    .getSystemService(Context.ACCESSIBILITY_SERVICE);
            accessibilityManager.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author zzc 电话监听器类
     */
    private class MobliePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // 挂机状态
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 通话状态

                case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
                    speakPauseNoTips();
                    break;
                default:
                    break;
            }
        }
    }

    Handler handler = new Handler();

    private void speakPause() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isSpeaking) {
                    showToast("暂停播放");
                }
                TatansApplication.stop();
                isSpeaking = false;
                setTitle("播放界面");
                pause_or_play.setText("播放");
                pause_or_play.setContentDescription("播放。按钮");
            }
        });

    }

    private void speakPauseNoTips() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TatansApplication.stop();
                isStopPlay = false;
                isSpeaking = false;
                setTitle("播放界面");
                pause_or_play.setText("播放");
                pause_or_play.setContentDescription("播放。按钮");
            }
        });

    }

    private void speakResume() {
        handler.post(new Runnable() {
            @Override
            public void run() {
//                showToast("继续播放");
                if (isStopPlay && isFlip) {
                    String[] nextSplit = adapter.getText().toString()
                            .split("，|。|！|？|；");
                    Log.i(TAG, "nextSplit[1]:" + nextSplit[1]);
                    sentenceIndex = map.get(nextSplit[1]) - 1;
                }
                setTitle("");
                isStopPlay = false;
                isFlip = false;

                isSpeaking = true;
                pause_or_play.setText("暂停");
                pause_or_play.setContentDescription("暂停。按钮");
                readNextSentence();
            }
        });
    }

    public void showToast(String text) {
        TatansToast.showAndCancel(text);
    }

}
