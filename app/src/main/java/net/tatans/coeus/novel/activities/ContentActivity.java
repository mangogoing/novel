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
import net.tatans.coeus.network.tools.TatansApplication;
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
import net.tatans.coeus.novel.tools.JudgeGarbledUtil;
import net.tatans.coeus.novel.tools.UrlUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    private List<ChapterDto> chapterList;
    private String filePath;
    private RequestQueue mRequestQueue;
    private int sourceNum = 0;
    private String source;
    private int totalChapterCount = 0;
    private boolean isFinalChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    private void init() {
        db = TatansDb.create(AppConstants.TATANS_DB_NAME);
        mRequestQueue = Volley.newRequestQueue(this);
        try {
            source = db.findById(bookId, CollectorDto.class)
                    .getSource();
        } catch (NullPointerException e) {
            source = "0";
        }
        sourceNum = Integer.parseInt(source);
        if (sourceNum == -1) {
            filePath = FilePathUtil.getFilePath(bookId, -2, sourceNum);
            try {
                StringBuffer sb = FileUtil.read(filePath);
                String result = sb.toString();
                result = result.replace("\r\n","");
                totalChapterCount = Integer.parseInt(result);
                filePath = FilePathUtil.getFilePath(bookId, currentPosition, sourceNum);
                new readFromSDcard().execute();

            } catch (Exception e) {
                setContent(title+"文件出错，无法阅读,试试重命名该小说，并刪除重新导入。");
                e.printStackTrace();
            }

            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                totalChapterCount = getLocalChapterCount(bookId, UrlUtil.CHAPTER_LIST_TXT, sourceNum);

                if (totalChapterCount == 0) {
                    // 网络请求
                    getSummaryResource(ContentActivity.this, mRequestQueue, bookId);
                } else {

                    playPlayback();
                }
            }
        }).start();

    }

    /**
     * @param bookId
     * @return 网站资源列表
     */
    private void getChapterListCount(final String bookId) {
        String bookBriefUrl = UrlUtil.RESOURCE_LIST + bookId;
        TatansHttp http = new TatansHttp();
        http.addHeader("User-Agent",
                "ZhuiShuShenQi/3.30.2(Android 5.1.1; TCL TCL P590L / TCL TCL P590L; )");
        http.get(bookBriefUrl, new HttpRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        List<SummaryDto> summarylist = JsonUtils.getSummaryListByJson(result.toString());
                        if (summarylist.size() > 0) {
                            int count = summarylist.get(sourceNum)
                                    .getChaptersCount();
                            Log.d(TAG, "本地共有章节：" + totalChapterCount + "---" + "最新共有章节：" + count);
                            if (count > totalChapterCount) {
                                totalChapterCount = count;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, String strMsg) {
                        HttpProces.failHttp();
                    }
                }
        );
    }

//    private void geLocalSummaryResource(String bookId, int sourceName, int sourceNum) {
//        String sourceFilePath = FilePathUtil.getFilePath(bookId, sourceName, sourceNum);
//        if (FileUtil.fileIsExists(sourceFilePath)) {
//            // 如果资源列表txt存在读取资源列表
//            String result = "";
//            try {
//                result = FileUtil.read(sourceFilePath).toString();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            List<SummaryDto> summarylist = JsonUtils.getSummaryListByJson(result);
//            if (summarylist.size() > 0) {
//                if (sourceNum > summarylist.size() - 1) {
//                    sourceNum = summarylist.size() - 1;
//                }
//                display();
//            } else {
//                showToast("读取小说文件失败，建议重新下载该资源");
//            }
//        }
//    }


    private int getLocalChapterCount(String bookId, int chapterName, int sourceNum) {
        int chapterCount = 0;
        String chapterFilePath = FilePathUtil.getFilePath(bookId, chapterName, sourceNum);
        if (FileUtil.fileIsExists(chapterFilePath) && isDownLoad != 3) {
            try {
                String result = FileUtil.read(chapterFilePath).toString();
                List<ChapterDto> chapterList = JsonUtils.getChapterListByJson(result);
                setChapterList(chapterList);
                chapterCount = chapterList.size();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (FileUtil.fileIsExists(chapterFilePath) && isDownLoad == 3) {
            try {
                chapterCount = FileUtil.read2Chapter(chapterFilePath)
                        .size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return chapterCount;
    }

    private void setChapterList(List<ChapterDto> chapterList) {
        this.chapterList = chapterList;
    }


    private void playPlayback() {
        filePath = FilePathUtil.getFilePath(bookId, currentPosition, sourceNum);
        if (FileUtil.fileIsExists(filePath)) {
            // 如果不为-1，并且该章节没有漏下则离线阅读
            getChapterListCount(bookId);
            new readFromSDcard().execute();
        } else {
            // 网络请求
            getSummaryResource(this, mRequestQueue, bookId);
        }
    }


    /**
     * @param context
     * @param bookId
     * @return 网站资源列表
     */
    private void getSummaryResource(Context context,
                                    final RequestQueue mRequestQueue, final String bookId) {
        String bookBriefUrl = UrlUtil.RESOURCE_LIST + bookId;
        TatansHttp http = new TatansHttp();
        http.addHeader("User-Agent",
                "ZhuiShuShenQi/3.30.2(Android 5.1.1; TCL TCL P590L / TCL TCL P590L; )");
        http.get(bookBriefUrl, new HttpRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        List<SummaryDto> summarylist = JsonUtils.getSummaryListByJson(result.toString());
                        if (summarylist.size() > 0) {
                            FileUtil.write(result.toString(),
                                    UrlUtil.SOURCE_LIST_TXT, bookId, 0);
                            if (sourceNum > summarylist.size() - 1) {
                                sourceNum = summarylist.size() - 1;
                            }
                            totalChapterCount = summarylist.get(sourceNum)
                                    .getChaptersCount();
                            getChapterResource(summarylist.get(sourceNum).get_id());
                        } else {
                            totalChapterCount = 10000;
                            setContent("未能请求到数据，请检查网络，若网络正常，在更多选项中换源试试吧。");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, String strMsg) {
                        HttpProces.failHttp();
                        totalChapterCount = 10000;
                        setContent("未能请求到数据，请检查网络，若网络正常，在更多选项中换源试试吧。");

                    }
                }

        );
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
                List<ChapterDto> chapterList = JsonUtils.getChapterListByJson(response.toString());
                setChapterList(chapterList);
                if (chapterList.size() > 0 && currentPosition < chapterList.size() - 2) {
                    FileUtil.write(response.toString(),
                            UrlUtil.CHAPTER_LIST_TXT, bookId, sourceNum);
                    getContentResource(chapterList.get(currentPosition)
                            .getLink(), currentPosition, true);
                    getContentResource(chapterList.get(currentPosition + 1)
                            .getLink(), currentPosition + 1, false);
                } else {
                    getContentResource(chapterList.get(chapterList.size() - 1)
                            .getLink(), currentPosition, true);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                totalChapterCount = 10000;
                setContent("未能请求到数据，请检查网络，若网络正常，在更多选项中换源试试吧。");
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        showToast("未能请求到数据，请检查网络");
//                        load.setText("未能请求到数据，请检查网络");
//                        load.setContentDescription("未能请求到数据，请检查网络");
//                    }
//                });
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
                totalChapterCount = 10000;
                Log.d("XXXXXXXXsssXX", error.toString());
                isFirstSetContent(error.toString(), isFirst);
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
        jr.setTag("NOVEL_REQUEST");
        mRequestQueue.add(jr);

    }

    private void isFirstSetContent(String result, boolean isFirst) {
        if (isFirst) {
            String str = JsonUtils.getNovelContent(result).replaceAll(
                    " ", "");
            str = str.replace("\n", "");
            if (isContainChinese(str) && !str.equals("") && chapterList.size() > 0) {
                try {
                    strContent = (currentPosition + 1) + "。"
                            + chapterList.get(currentPosition).getTitle() + "：" + "\n正文："
                            + str;
                } catch (Exception e) {
                    strContent = "未能请求到数据，请检查网络，若网络正常，在更多选项中换源试试吧。";
                }

            } else {
                strContent = "未能请求到数据，请检查网络，若网络正常，在更多选项中换源试试吧。";
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
            String result = "";
            try {
                result = FileUtil.read(filePath).toString();
            } catch (IOException e) {
                e.printStackTrace();
                setContent(title+"小说内容出错，建议换源重新下载该资源");
                return;
            }
            json3Gson(result);
            if (!strContent.equals("")) {
                setContent(strContent);
            } else {
                setContent(title+"小说内容出错，建议换源重新下载该资源");
            }
        }

    }

    private String json3Gson(String result) {
        strContent = "";
        if (isDownLoad != 3) {
            String str = JsonUtils.getNovelContent(result).replaceAll(" ", "");
            str = str.replace("\n", "");
            if (isContainChinese(str) && chapterList.size() > 0) {
                try {
                    strContent = (currentPosition + 1) + "。"
                            + chapterList.get(currentPosition).getTitle() + "：" + "\n正文："
                            + str;
                } catch (Exception e) {
                    strContent = "未能请求到数据，请检查网络，若网络正常，在更多选项中换源试试吧。";
                }
            } else {
                countPage = 0;
                sentenceIndex = -1;
                position = 0;
                return strContent = "";
            }

        } else {
            JudgeGarbledUtil.isMessyCode(result);
            String mResult = result.replace(" ", "");
            mResult = mResult.replace("\r\n", "");
            strContent =  (currentPosition + 1)+"。"+mResult;
        }

        if (isCollector) {
            isCollector = false;
        } else {
            countPage = 0;
            sentenceIndex = -1;
            position = 0;
        }

        return strContent;
    }

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
        dwonLoadNextChapter();
    }

    private void dwonLoadNextChapter() {
        if (currentPosition < totalChapterCount - 1) {
            currentPosition++;
            playPlayback();
            int nextPosition = currentPosition + 1;
            String path = FilePathUtil.getFilePath(bookId, nextPosition, sourceNum);
            if (!FileUtil.fileIsExists(path) && chapterList != null) {
                if (nextPosition < (chapterList.size() - 1)) {
                    getContentResource(chapterList.get(nextPosition).getLink(), nextPosition, false);
                }
            }
        } else {
            showToast("没有下一章了,已退出播放界面，");
            countPage = 0;
            sentenceIndex = -1;
            position = 0;
            save();
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                load.setVisibility(View.VISIBLE);
            }
        });
        dwonLoadPreChapter();

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
            TatansApplication.pause();
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