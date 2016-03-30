package net.tatans.coeus.novel.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.activities.CollectorActivity;
import net.tatans.coeus.novel.activities.ContentActivity;
import net.tatans.coeus.novel.constant.AppConstants;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.services.DownLoadService;
import net.tatans.coeus.novel.tools.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的书架adapter
 *
 * @author shiyunfei
 */
public class CollectorAdapter extends BaseAdapter {
    private List<CollectorDto> CollectorList;
    private List<CollectorDto> list;
    private ProgressDialog proDia;
    // 章节更新map
    Map<String, Integer> map = new HashMap<String, Integer>();
    private Context context;
    private boolean isDelete = true;
    Handler handler = new Handler();
    /**
     * page暂时无用，本可做分页处理，但已在activity做了
     */
    private float pageSize;
    private int to, from, currentPage;
    TatansDb db = TatansDb.create(AppConstants.TATANS_DB_NAME);
    private String dailog = "";

    public CollectorAdapter(List<CollectorDto> list, Context context,
                            float pageSize, int currentPage, Map<String, Integer> map, ProgressDialog proDia) {

        super();
        this.map = map;
        this.list = list;
        this.context = context;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.proDia = proDia;
        init();
    }

    private void init() {
        to = (int) (pageSize + pageSize * (currentPage - 1));
        from = (int) (pageSize * (currentPage - 1));
        if (to > list.size()) {
            to = list.size();
        }
//		CollectorList = list.subList(from, to);
        CollectorList = list;
    }

    @Override
    public int getCount() {
        return CollectorList.size();
    }

    @Override
    public Object getItem(int position) {
        return CollectorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.collector_item, parent, false);
        }
        final CollectorDto item = CollectorList.get(position);
        final String _id = item.get_id();
        final String title = item.getTitle();
        LinearLayout reading = (LinearLayout) convertView
                .findViewById(R.id.reading);
        ImageView status = (ImageView) convertView.findViewById(R.id.status);
        TextView textView = (TextView) convertView
                .findViewById(R.id.collector_name);
        String text = title;
        final CollectorDto collcetor = db.findById(_id, CollectorDto.class);
        final ImageView imageView = (ImageView) convertView
                .findViewById(R.id.collector_del);
        // 收藏的小说检测更新
        if (map.containsKey(_id)) {
            int netChapterCount = map.get(_id);
            if (collcetor != null && netChapterCount != -1
                    && collcetor.getChaperCount() != 0) {
                int number = netChapterCount - collcetor.getChaperCount();
                if (number > 0) {
                    text = text + "：有" + number + "章更新";

                }
            }
        }
        /**
         * 设置text
         */
        if (collcetor != null) {
            if (collcetor.getIsDownLoad() == 0) {
                // 若为正在下载，显示进度
                if (convertView.getTag() != null) {
                    String Tag = ": " + (double) convertView.getTag() + "%已缓存";
                    text = text + Tag;
                }
                imageView.setContentDescription("取消緩存" + title );
                status.setImageResource(R.drawable.down_loading);
                status.setContentDescription("下载中");
            } else if (collcetor.getIsDownLoad() == 2) {
                // 若为列队下载
                text = text + ": 等待缓存中";
                imageView.setContentDescription("取消緩存" + title);
                status.setImageResource(R.drawable.be_queued);
                status.setContentDescription("等待中");
            } else if (collcetor.getIsDownLoad() == 1) {
                imageView.setContentDescription("移除小说" + title);
                status.setImageResource(R.drawable.down_loaded);
                status.setContentDescription("已下载");
            } else if (collcetor.getIsDownLoad() == 3) {
                imageView.setContentDescription("移除小说" + title );
                status.setImageResource(R.drawable.local_file);
                status.setContentDescription("本地小说");
            } else {
                // 仅仅收藏过
                imageView.setContentDescription("取消收藏" + title);
                status.setImageResource(R.drawable.collected);
                status.setContentDescription("已收藏");
            }
        }
        // 设置文字
        textView.setText(text);
        // 设置文字
        textView.setContentDescription(text + "。按钮");
        /**
         * 删除键的点击事件
         */

        imageView.setOnClickListener(new OnClickListener() {
            // 删除
            @Override
            public void onClick(View v) {
                proDia.show();
                try {
                    if (isDelete) {
                        isDelete = false;
                        String filePath = Environment.getExternalStorageDirectory()
                                + "/tatans/novel/" + _id;
                        final File file = new File(filePath);
                        CollectorDto item = CollectorList.get(position);
                        String _id = item.get_id();
                        CollectorDto collcetor = db.findById(_id,
                                CollectorDto.class);
                        // 如果删除的是正在下载的小说，则停止下载
                        int downloadState = collcetor.getIsDownLoad();
                        if (downloadState == 0) {
                            // 停止下载service
                            Intent Intent = new Intent(context,
                                    DownLoadService.class);
                            context.stopService(Intent);
                            dailog = "取消下载并";
                            startDownLoadNext();
                        }
                        new Thread(new Runnable() {
                            public void run() {
                        FileUtil.delete(file);
                            }
                        }).start();


                        db.delete(item);
                        list.remove(item);
                        CollectorActivity.pageCount = (int) Math.ceil(list.size()
                                / AppConstants.APP_PAGE_SIZE);
                        CollectorList = list;
//                    if (list.size() == 0) {
//                        handler.postDelayed(flushFinsh, 10);
//                    } else {
                        handler.postDelayed(flush, 10);
//                    }
//                  else if (CollectorList.size() == 0 && currentPage != 1) {
//                            currentPage--;
//                            init();
//                            handler.postDelayed(flush, 10);
//                        } else if (list.size() <= pageSize) {
//                            currentPage = 1;
//                            init();
//                            handler.postDelayed(flush, 10);
//                        } else {
//                            init();

//                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            proDia.dismiss();
                            TatansToast.showAndCancel(title + dailog + "移除成功");
                        }
                    }, 2000);
                }
            }
        });
        /**
         * 进入该收藏小说的阅读界面
         */
        reading.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new send(_id, title), 300);
            }
        });
        return convertView;
    }

    Runnable isDelay = new Runnable() {

        @Override
        public void run() {
            isDelete = true;
        }
    };

    Runnable flush = new Runnable() {

        @Override
        public void run() {
            notifyDataSetChanged();
            handler.postDelayed(isDelay, 2000);
        }
    };


    public class send implements Runnable {
        String _id;
        String title;

        public send(String _id, String title) {
            super();
            this._id = _id;
            this.title = title;
        }

        @Override
        public void run() {
            sendIntent(_id, title);

        }

    }

    /**
     * 跳到阅读界面
     */
    private void sendIntent(String _id, String title) {
        int currentPosition = db.findById(_id, CollectorDto.class)
                .getSavedPosition();
        int isDownLoad = db.findById(_id, CollectorDto.class).getIsDownLoad();
        int countPage = db.findById(_id, CollectorDto.class).getCountPage();
        int sentenceIndex = db.findById(_id, CollectorDto.class)
                .getSentenceIndex();
        int position = db.findById(_id, CollectorDto.class).getPosition();
        int totalChapterCount = db.findById(_id, CollectorDto.class)
                .getChaperCount();
        String source = db.findById(_id, CollectorDto.class).getSource();
        Intent intent = new Intent();
        intent.setClass(context, ContentActivity.class);
        intent.putExtra("isDownLoad", isDownLoad);
        intent.putExtra("bookId", _id);
        intent.putExtra("source", source);
        intent.putExtra("totalChapterCount", totalChapterCount);
        intent.putExtra("isCollector", true);
        intent.putExtra("currentPosition", currentPosition);
        intent.putExtra("title", title);
        intent.putExtra("countPage", countPage);
        intent.putExtra("sentenceIndex", sentenceIndex);
        intent.putExtra("position", position);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 下载下一个等待的小说
     */
    private void startDownLoadNext() {

        List<CollectorDto> CollectorDtoList = db.findAllByWhere(
                CollectorDto.class, " isDownLoad ==2 ", " date desc");
        // 把第一个等待缓存的改为正在下载
        if (CollectorDtoList.size() != 0) {
            CollectorDto firstItem = CollectorDtoList.get(0);
            firstItem.setIsDownLoad(0);
            db.update(firstItem);
            // 打开service下载下一个等待缓存的
            final Intent downLoadIntent = new Intent(context,
                    DownLoadService.class);
            downLoadIntent.putExtra("bookId", CollectorDtoList.get(0)
                    .get_id());
            downLoadIntent
                    .putExtra("title", CollectorDtoList.get(0).getTitle());
            downLoadIntent
                    .putExtra("source", CollectorDtoList.get(0).getSource());
            Handler handler = new Handler();
            // 延时两秒
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    context.startService(downLoadIntent);

                }
            }, 200);

        }
    }

}
