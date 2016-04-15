package net.tatans.coeus.novel.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ushaqi.zhuishushenqi.util.CipherUtil;

import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.util.HttpProces;
import net.tatans.coeus.novel.dto.ChapterDto;
import net.tatans.coeus.novel.dto.SummaryDto;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.JsonUtils;
import net.tatans.coeus.novel.tools.SharedPreferencesUtil;
import net.tatans.coeus.novel.tools.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载service
 *
 * @author shiyunfei
 */
public class DownLoadService extends Service {
    private static final String TAG = "LocalService";
    private int size = 1;
    private String bookId;
    private String title;
    private RequestQueue mRequestQueue;
    private List<ChapterDto> chapterlist = new ArrayList<ChapterDto>();
    private String viewChaptersUrl = "";
    private int sourceNum;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        Log.d("IIIIIIIIII", "-------------1------------" + System.currentTimeMillis() + "");

        if (intent != null) {
            chapterlist.clear();
            bookId = intent.getStringExtra("bookId");
            title = intent.getStringExtra("title");
            sourceNum = Integer.parseInt(intent.getStringExtra("source"));
            mRequestQueue = Volley.newRequestQueue(this);
            showToast("准备缓存" + title);
            if (bookId != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showToast("开始缓存" + title);
                        init();
                    }
                }, 3000);
            }
        }

        return START_REDELIVER_INTENT;
    }

    /**
     * 获取源id 默认easou
     */
    private void init() {
        String url = UrlUtil.RESOURCE_LIST + bookId;
        StringRequest jrTocs = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d("IIIIIIIIII", "-------------2------------" + System.currentTimeMillis() + "");
                        /** 目錄写入.TXT文件保存 */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FileUtil.write(response.toString(),
                                        UrlUtil.SOURCE_LIST_TXT, bookId, 0);
                                AnalyzeTocs(response.toString());
                            }
                        }).start();


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error + "");
                showToast("未能获取到资源列表，请检查网络");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");
                headers.put("User-Agent",
                        "ZhuiShuShenQi/3.30.2(Android 5.1.1; TCL TCL P590L / TCL TCL P590L; )");
                // "application/x-javascript");
                // headers.put("Content-Type",
                // headers.put("Accept-Encoding", "gzip");
                return headers;
            }
        };
        mRequestQueue.add(jrTocs);
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                String bookBriefUrl = UrlUtil.RESOURCE_LIST + bookId;
//                TatansHttp http = new TatansHttp();
//                http.addHeader("User-Agent",
//                        "ZhuiShuShenQi/3.30.2(Android 5.1.1; TCL TCL P590L / TCL TCL P590L; )");
//                http.get(bookBriefUrl, new HttpRequestCallBack<String>() {
//                    @Override
//                    public void onSuccess(String result) {
//                        super.onSuccess(result);
//                        /** 目錄写入.TXT文件保存 */
//                        FileUtil.write(result.toString(),
//                                UrlUtil.SOURCE_LIST_TXT, bookId, 0);
//                        AnalyzeTocs(result.toString());
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t, String strMsg) {
//                        HttpProces.failHttp();
//                        showToast("未能获取到资源列表，请检查网络");
//
//                    }
//                });
//            }
//
//        }).start();
    }

    /**
     * 获取章节列表
     */
    private void AnalyzeTocs(final String string) {

        List<SummaryDto> summarylist = JsonUtils.getSummaryListByJson(string);
        if (summarylist.size() > 0) {
            if (sourceNum > summarylist.size() - 1) {
                sourceNum = summarylist.size() - 1;
            }
            viewChaptersUrl = UrlUtil.VIEW_CHAPTERS
                    + summarylist.get(sourceNum).get_id() + "?view=chapters";
        }

        StringRequest jrChapterLists = new StringRequest(Request.Method.GET,
                viewChaptersUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.d("IIIIIIIIII", "-------------3------------" + System.currentTimeMillis() + "");
                /** 目錄写入.TXT文件保存 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtil.write(response.toString(),
                                UrlUtil.CHAPTER_LIST_TXT, bookId, sourceNum);
                        downLoadChapters(response);
                    }
                }).start();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("未能请求到数据，请检查网络");
                stopSelf();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll("NOVEL_REQUEST");
        }

    }

    // 定义内容类继承Binder
    public class LocalBinder extends Binder {
        // 返回本地服务
        DownLoadService getService() {
            return DownLoadService.this;
        }
    }


    private void showToast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TatansToast.showAndCancel(text);
            }
        });
    }

    Handler handler = new Handler();


    // 開啟volley网络下載章節內容
    public void downLoadChapters(String response) {
        JSONObject json;
        try {
            json = new JSONObject(response);
            JSONArray array;
            try {
                array = json.getJSONArray("chapters");
                size = array.length();
                String Path = Environment.getExternalStorageDirectory()
                        + "/tatans/novel/" + bookId + "/";
                for (int i = 0; i < array.length(); i++) {
                    String filePath = Path + i + "_" + sourceNum + ".txt";
                    final int j = i;
                    if (!fileIsExists(filePath)) {
                        String httpLink = "";
                        try {
                            httpLink = "/chapter/"
                                    + URLEncoder.encode(array.getJSONObject(j)
                                    .getString("link"), "UTF8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String k_t = CipherUtil.getKey_t(httpLink);
                        String url = "http://chapter2.zhuishushenqi.com"
                                + httpLink + "?" + k_t;
                        Log.d("WWWWWWWWWWW", url);
                        JsonObjectRequest jr = new JsonObjectRequest(
                                Request.Method.GET, url,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(final JSONObject response) {
                                        Log.d("IIIIIIIIII", "-------------4------------" + System.currentTimeMillis() + "");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                FileUtil.write(response.toString(), j,
                                                        bookId, sourceNum);
                                                send(j, true);
                                            }
                                        }).start();
                                        /*
                                         * if (j % 80 == 0) { send(j, true); }
										 * else if (chapterlist.size() - 1 == j)
										 * { send(j, true); }
										 */


                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(
                                    VolleyError error) {
                                        /*
                                         * Log.d("MYTAG", j + ""); if (j % 80 ==
										 * 0) { send(j, true); } else if
										 * (chapterlist.size() - 1 == j) {
										 * send(j, true); }
										 */
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        send(j, true);
                                    }
                                }).start();
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
                    } else {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                send(j, true);

                            }
                        }).start();

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    public boolean fileIsExists(String filePath) {
        boolean isExist = false;
        try {
            File f = new File(filePath);
            if (f.exists()) {
                isExist = true;
            }

        } catch (Exception e) {
            isExist = false;
        }
        return isExist;
    }

    // 控制resend方法不死循环，重新发送最多一百次

	/*
     * Runnable downLoadChapters = new Runnable() {
	 * 
	 * @Override public void run() { downLoadChapters(); } };
	 */

    double max = 0;
    double percent = 0;

    // 发送广播下載百分比
    private void send(int j, Boolean flag) {
        Intent intent = new Intent();
        intent.setAction(UrlUtil.ACTION);
        percent = Math.floor((j + 1) * 1000 / size) / 10;
        if (percent == 100) {
            // 下载完成停止该service
            Intent finishIntent = new Intent(UrlUtil.FINISH_ACTION);
            finishIntent.putExtra("bookId", bookId);
            sendBroadcast(finishIntent);
            stopSelf();
        }
        Log.d("percent", percent + ":" + j);
        // 第一次请求数据发送id刷新进度条，resend则不发送以免进度条错乱
        intent.putExtra("bookId", bookId);
        if (percent > max) {
            max = percent;
        }
        if (max > percent) {
            percent = max;
        }

        if (percent != 0) {
            intent.putExtra("percent", percent);
            Log.e("AAAA", "percent-22-" + percent);
            sendBroadcast(intent);
        }

    }

}
