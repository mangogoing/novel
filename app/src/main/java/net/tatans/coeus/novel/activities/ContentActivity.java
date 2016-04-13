package net.tatans.coeus.novel.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ushaqi.zhuishushenqi.util.CipherUtil;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.util.HttpProces;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.ChapterDto;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.dto.SummaryDto;
import net.tatans.coeus.novel.tools.FilePathUtil;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.JsonUtils;
import net.tatans.coeus.novel.tools.SharedPreferencesUtil;
import net.tatans.coeus.novel.tools.UrlUtil;
import net.tatans.coeus.util.Callback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yuliang
 * @move Siliping 2014-09-19
 * @move luojianqin 左右滑动翻页 左：下一页 如果没有下一页了就是下一条资讯 右：上一页 会根据设置是否自动播报下一条资讯 双指上滑：暂停
 * 双指下滑：播放 home键暂停，关闭屏幕不暂停播放
 */
public class ContentActivity extends ContentSplitActivity {
    private List<CollectorDto> lt_collector; // 小说的collector list
    private static String TAG = "ContentActivity";
    private String strContent = ""; // 资讯内容
    private TatansDb db;
    private String chapterFilePath, sourceFilePath;
    private List<ChapterDto> ChapterList;
    private String filePath;
    private RequestQueue mRequestQueue;
    private int sourceNum = 0;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = TatansDb.create(AppConstants.TATANS_DB_NAME);
        mRequestQueue = Volley.newRequestQueue(this);
        init();

    }

    private void init() {
        try {
            source = db.findById(bookId, CollectorDto.class)
                    .getSource();
        } catch (NullPointerException e) {
            source = "0";
        }

        sourceNum = Integer.parseInt(source);
        sourceFilePath = FilePathUtil.getFilePath(bookId, UrlUtil.SOURCE_LIST_TXT, 0);
        if (isDownLoad == 1 && FileUtil.fileIsExists(sourceFilePath)
                || isDownLoad == 3 && FileUtil.fileIsExists(sourceFilePath)
                || isDownLoad == 0 && FileUtil.fileIsExists(sourceFilePath) || isDownLoad == -1 && FileUtil.fileIsExists(sourceFilePath)) {
            // 如果资源列表txt存在读取资源列表
            new readSummaryListFromSDcard().execute();

        } else {
            // 网络请求
            getSummaryResource(this, mRequestQueue, bookId);
        }
    }


    class readSummaryListFromSDcard extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                String result = FileUtil.read(sourceFilePath).toString();
                summarylist = JsonUtils.getSummaryListByJson(result);
                if (summarylist.size() > 0) {
                    if (sourceNum > summarylist.size() - 1) {
                        sourceNum = summarylist.size() - 1;
                    }
                    display();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private void display() {
        chapterFilePath = FilePathUtil.getFilePath(bookId, -1, sourceNum);
        if (isDownLoad == 1 && FileUtil.fileIsExists(chapterFilePath)
                || isDownLoad == 0 && FileUtil.fileIsExists(chapterFilePath)) {
            String result;
            try {
                result = FileUtil.read(chapterFilePath).toString();
                ChapterList = JsonUtils.getChapterListByJson(result);
                totalChapterCount = ChapterList.size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (FileUtil.fileIsExists(chapterFilePath) && isDownLoad == 3) {
            try {
                totalChapterCount = FileUtil.read2Chapter(chapterFilePath)
                        .size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playPlayback();
    }


    private void playPlayback() {

        filePath = FilePathUtil.getFilePath(bookId, currentPosition, sourceNum);

//        if (isDownLoad == 1 && FileUtil.fileIsExists(filePath)
//                || isDownLoad == 3 && FileUtil.fileIsExists(filePath)
//                || isDownLoad == 0 && FileUtil.fileIsExists(filePath)) {
        if (FileUtil.fileIsExists(filePath)) {
            // 如果不为-1，并且该章节没有漏下则离线阅读
            new readFromSDcard().execute();
        } else {
            // 网络请求
            getSummaryResource(this, mRequestQueue, bookId);
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
                totalChapterCount = 1000;
                setContent("未能获取到资源，请检查网络吧");

            }
        });
    }

    /**
     * 获取章节列表
     */
    private void getChapterResource(final String id) {
        String viewChaptersUrl = "";
        viewChaptersUrl = UrlUtil.VIEW_CHAPTERS
                + id + "?view=chapters";

        StringRequest jrChapterLists = new StringRequest(Request.Method.GET,
                viewChaptersUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                ChapterList = JsonUtils.getChapterListByJson(response.toString());
                if (ChapterList.size() > 0 && currentPosition < ChapterList.size() - 2) {
                    getContentResource(ChapterList.get(currentPosition)
                            .getLink(), currentPosition, true);
                    getContentResource(ChapterList.get(currentPosition + 1)
                            .getLink(), currentPosition + 1, false);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("未能请求到数据，请检查网络");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent",
                        "YouShaQi/2.23.2 (iPhone; iOS 9.2; Scale/2.00)");
                headers.put("Content-Encoding", "gzip");
                headers.put("Content-Type", " application/json; charset=utf-8");
                return headers;
            }

        };
        mRequestQueue.add(jrChapterLists);
    }

    /**
     * @param
     * @param newBookId
     * @return 网站资源列表
     */
//    private void getChapterResource(final String newBookId) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                HttpURLConnection cumtConnection;
//                InputStream urlStream = null;
//                BufferedReader reader = null;
//                String result = "";
//                try {
//                    cumtConnection = (HttpURLConnection) new URL(
//                            UrlUtil.RESOURCE_BOOK_ID + newBookId
//                                    + "?view=chapters").openConnection();
//                    cumtConnection.setRequestProperty("User-Agent",
//                            "YouShaQi/2.23.2 (iPhone; iOS 9.2; Scale/2.00)");
//                    urlStream = cumtConnection.getInputStream();
//                    reader = new BufferedReader(
//                            new InputStreamReader(urlStream, "UTF-8"));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        result = result + line;
//                        Log.d("XXXXXXXXXX", line.toString());
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        reader.close();
//                        urlStream.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (result.equals("")) {
//                    setContent("未能获取到资源，在更多选项中选择其他资源试试吧");
//                } else {
//                    try {
//                        ChapterList = JsonUtils.getChapterListByJson(result);
//                        getContentResource(ChapterList.get(currentPosition)
//                                .getLink(), currentPosition, true);
////                        getContentResource(ChapterList.get(currentPosition + 1)
////                                .getLink(), currentPosition + 1, false);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        setContent("未能获取到资源，在更多选项中选择其他资源试试吧");
//                    }
//                }
//            }
//        }
//        ).start();
//    }
    private void getContentResource(final String link, final int chapterPosition, final boolean isFirst) {
        String url = "";
        String url1 = "http://chapter2.zhuishushenqi.com";
        String url2 = null;
        try {
            url2 = "/chapter/" + URLEncoder.encode(link, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String key = CipherUtil.getKey_t(url2);
        url = url1 + url2 + "?" + key;
        Log.d("XXXXXXXXsssXX", url.toString());
        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET, url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d("XXXXXXXXsssXX", response.toString());
                        FileUtil.write(response.toString(), chapterPosition,
                                bookId, sourceNum);
                        isFirstSetContent(response.toString(), isFirst);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(
                    VolleyError error) {
                Log.d("XXXXXXXXsssXX",error.toString());
                isFirstSetContent(error.toString(), isFirst);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent",
                        "YouShaQi/2.23.2 (iPhone; iOS 9.2; Scale/2.00)");
                headers.put("Content-Encoding", "gzip");
                headers.put("Content-Type", " application/json; charset=utf-8");
                return headers;
            }
        };
        jr.setTag("NOVEL_REQUEST");
        mRequestQueue.add(jr);

    }

    private void isFirstSetContent(String result, boolean isFirst) {
        if (isFirst) {
            String str = JsonUtils.getNovelContent(result).replaceAll(
                    " ", "");
            str = str.replace("\n", "");
            if (isContainChinese(str) && !str.equals("")) {
                strContent = (currentPosition + 1) + "。"
                        + ChapterList.get(currentPosition).getTitle() + "：" + "\n正文："
                        + str;
            } else {
                strContent = "未能获取到资源，在更多选项中选择其他资源试试吧";
                countPage = 0;
                sentenceIndex = -1;
                position = 0;
            }
            if (isCollector) {
                isCollector = false;
            } else {
                countPage = 0;
                sentenceIndex = -1;
                position = 0;
            }
            setContent(strContent);
        }
    }

    private boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    // 从sd卡读取数据并解析显示
    class readFromSDcard extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // isCollector = true;
            String result;
            try {
                result = FileUtil.read(filePath).toString();
                json3Gson(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!strContent.equals("")) {
                setContent(strContent);
            } else {
                setContent("小说内容出错，建议换源重新下载该资源");
            }
        }

    }

    private String json3Gson(String result) {
        strContent = "";
        if (isDownLoad != 3) {
            String str = JsonUtils.getNovelContent(result).replaceAll(" ", "");
            str = str.replace("\n", "");
            if (isContainChinese(str)) {
                strContent = (currentPosition + 1) + "。"
                        + ChapterList.get(currentPosition).getTitle() + "：" + "\n正文："
                        + str;
            }

        } else {
            String mResult = result.replace(" ", "");
            String mmResult = mResult.replace("\n", "");
            strContent = mmResult;
        }

        if (isCollector) {
            // countPage = intent.getIntExtra("countPage", 0);
            // sentenceIndex = intent.getIntExtra("sentenceIndex", -1);
            // position = intent.getIntExtra("position", 0);
            isCollector = false;
        } else {
            countPage = 0;
            sentenceIndex = -1;
            position = 0;
        }

        return strContent;
    }

//	// 从sd卡读取数据并解析显示
//	class readChapterFromSDcard extends AsyncTask<Void, Void, String> {
//
//		@Override
//		protected String doInBackground(Void... params) {
//			try {
//				String result = FileUtil.read(chapterFilePath).toString();
//				ChapterList = JsonUtils.getChapterListByJson(result);
//				totalChapterCount = ChapterList.size();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			if (!strContent.equals("")) {
//				setContent(strContent);
//			} else {
//				showToast("小说内容出错，建议删除重新下载该资源");
//			}
//		}
//
//	}

    /**
     * 下一条资讯
     */
    @Override
    public void nextInformation() {
        super.nextInformation();
        nextChapter();
    }

    public void nextChapter() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                load.setVisibility(View.VISIBLE);
            }
        });
//        if (isDownLoad != -1) {
        dwonLoadNextChapter();
//            return;
//        } else {
//            if (currentPosition < totalChapterCount - 1) {
//                currentPosition++;
//                getContentResource(ChapterList.get(currentPosition).getLink(),false);
//            } else {
//                showToast("没有下一章了");
//                load.setVisibility(View.GONE);
//                finish();
//            }

//    }

    }

    private void dwonLoadNextChapter() {
        if (currentPosition < totalChapterCount - 1) {
            currentPosition++;
            playPlayback();
            if (currentPosition + 1 < totalChapterCount - 2) {
                try {
                    getContentResource(ChapterList.get(currentPosition + 1).getLink(), currentPosition + 1, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    setContent("未能获取到资源，在更多选项中选择其他资源试试吧");
                }
            }
        } else {
            showToast("没有下一章了");
            load.setVisibility(View.GONE);
            countPage = 0;
            sentenceIndex = -1;
            position = 0;
            finish();
        }

    }

    /**
     * 上一条资讯
     */
    @Override
    public void preInformation() {
        super.preInformation();
        preChapter();
    }

    public void preChapter() {

        load.setVisibility(View.VISIBLE);
//        if (isDownLoad != -1) {
        dwonLoadPreChapter();
//        return;
//        } else {
//            currentPosition--;
//            if (currentPosition < 0) {
//                currentPosition = 0;
//                showToast("没有上一章了");
//            }
//            getContentResource(ChapterList.get(currentPosition).getLink(),false);
//        }

    }

    private void dwonLoadPreChapter() {
        currentPosition--;
        if (currentPosition < 0) {
            currentPosition = 0;
            showToast("没有上一章了");
        }
        playPlayback();

    }

    @Override
    public void gotoMore() {
        super.gotoMore();
        Intent intentMore = new Intent();
        intentMore.setClass(ContentActivity.this, MoreActivity.class);
        intentMore.putExtra("isDownLoad", isDownLoad);
        intentMore.putExtra("title", title);
        intentMore.putExtra("currentPosition", currentPosition);
        intentMore.putExtra("bookId", bookId);
        intentMore.putExtra("currentPosition", currentPosition);
        intentMore.putExtra("totalChapterCount", totalChapterCount);
        startActivityForResult(intentMore, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                load.setVisibility(View.VISIBLE);
                countPage = 0;
                sentenceIndex = -1;
                position = 0;
                isDownLoad = data.getIntExtra("isDownLoad", -1);
                currentPosition = data.getIntExtra("currentPosition", 0);
                title = data.getStringExtra("title");
                totalChapterCount = data.getIntExtra("totalChapterCount", 0);
                bookId = data.getStringExtra("bookId");
                sourceNum = data.getIntExtra("sourceNum", 0);
                init();
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                boolean isCollect = data.getBooleanExtra("isCollect", false);
                if (isCollect) {
                    CollectorDto collector = new CollectorDto(bookId, title,
                            currentPosition, -1, new Date(), totalChapterCount,
                            countPage, sentenceIndex, position, source);
                    db.save(collector);
                    finish();
                    showToast("收藏成功");
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speaker != null) {
            speaker.stop();
            speaker.setOnSpeechCompletionListener(null);
            speaker = null;
        }
//		save();
    }

    /**
     * 记录播放历史，若为收藏的小说，保存章节位置
     *
     * @author shiyunfei
     */
    private void save() {
        lt_collector = db.findAll(CollectorDto.class);
        if (lt_collector.size() == 0) {
            return;
        }
        for (int i = 0; i < lt_collector.size(); i++) {
            if (lt_collector.get(i).get_id().equals(bookId)) {
                CollectorDto item = db.findById(bookId, CollectorDto.class);
                if (currentPosition < 0) {
                    currentPosition = 0;
                }
                CollectorDto collector = new CollectorDto(bookId,
                        item.getTitle(), currentPosition, item.getIsDownLoad(),
                        new Date(), totalChapterCount, countPage,
                        sentenceIndex, position, source);
                db.update(collector);
                return;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (speaker != null) {
                speaker.stop();
                speaker.setOnSpeechCompletionListener(null);
                speaker = null;
            }
            mAudioManagerUtil.abandonAudioFocus();
            if (hasCollectored() == false) {
                prompt();
            } else {
                save();
                this.finish();
            }
        }
        return false;
    }

    private void prompt() {
        Intent intentMore = new Intent();
        intentMore.setClass(ContentActivity.this, PromptActivity.class);
        startActivityForResult(intentMore, 2);

    }

    private void dialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ContentActivity.this);
        alertDialogBuilder.setMessage("您还未收藏该小说，是否收藏");
        alertDialogBuilder.setTitle("提示");
        alertDialogBuilder.setPositiveButton("收藏。",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        CollectorDto collector = new CollectorDto(bookId,
                                title, currentPosition, -1, new Date(),
                                totalChapterCount, countPage, sentenceIndex,
                                position, source);
                        db.save(collector);
                        appSpeaker.speech("收藏成功", new Callback() {
                            @Override
                            public void onDone() {
                                super.onDone();
                                showToast("收藏成功");
                                dialog.dismiss();
                                ContentActivity.this.finish();
                            }
                        });

                    }
                });
        alertDialogBuilder.setNegativeButton("取消。",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ContentActivity.this.finish();
                    }
                });
        alertDialogBuilder.create().show();
    }

    // 判断是否收藏过
    private boolean hasCollectored() {
        List<CollectorDto> lt_collector = db.findAll(CollectorDto.class);
        if (lt_collector.size() == 0) {
            return false;
        }
        List<String> list_id = new ArrayList<String>();
        for (int i = 0; i < lt_collector.size(); i++) {
            list_id.add(lt_collector.get(i).get_id());
        }
        if (list_id.contains(bookId)) {
            return true;
        } else {
            return false;
        }
    }

}