package net.tatans.coeus.novel.broadCastRecivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.dto.CollectorDto;
import net.tatans.coeus.novel.services.DownLoadService;
import net.tatans.coeus.novel.tools.FileUtil;
import net.tatans.coeus.novel.tools.NetworkUtils;
import net.tatans.coeus.novel.tools.UrlUtil;

import java.io.File;
import java.util.List;

public class DownLoadFinishReciver extends BroadcastReceiver {
    public TatansDb db;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // 停止下载service
        if (!intent.getAction().equals(UrlUtil.FINISH_ACTION)) {
            return;
        }
        boolean replace = intent.getBooleanExtra("replace", false);
        boolean isLoading = intent.getBooleanExtra("isLoading", false);
        if (!replace) {
            Intent Intent = new Intent(context, DownLoadService.class);
            context.stopService(Intent);
        }
        if (isLoading) {
            Intent Intent = new Intent(context, DownLoadService.class);
            context.stopService(Intent);
        }
        db = TatansDb.create("MyCollector");
        String bookId = intent.getStringExtra("bookId");
        CollectorDto item = db.findById(bookId, CollectorDto.class);
        if (!NetworkUtils.isNetworkConnected(context)) {
            TatansToast.showAndCancel("您的网络已断开，缓存过程被终止");
            if (item != null) {
                // 删除已下载的部分文件
                String filePath = Environment.getExternalStorageDirectory()
                        + "/tatans/novel/" + bookId;
                final File file = new File(filePath);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            public void run() {
                                FileUtil.delete(file);
                            }
                        }).start();
                    }
                },3000);
                // 把该小说标记为等待下载
                item.setIsDownLoad(2);
                db.update(item);
            }
            /*
             * List<CollectorDto> isQueuedList = db.findAllByWhere(
			 * CollectorDto.class, " isDownLoad=2"); if (isQueuedList.size() !=
			 * 0) { for (int i = 0; i < isQueuedList.size(); i++) {
			 * db.delete(isQueuedList.get(i)); } }
			 */
        } else {

            if (item != null) {
                if (replace) {
                    // 删除已下载的部分文件
                    String filePath = Environment.getExternalStorageDirectory()
                            + "/tatans/novel/" + bookId;
                    final File file = new File(filePath);
                    new Thread(new Runnable() {
                        public void run() {
                            FileUtil.delete(file);
                        }
                    }).start();
                    // 把该小说标记为已收藏-1
                    item.setIsDownLoad(-1);
                    db.update(item);
                } else {
                    // 把该小说标记为已下载1
                    item.setIsDownLoad(1);
                    db.update(item);
                    TatansToast.showAndCancel(item.getTitle() + " 缓存完成");
                }
            }
            List<CollectorDto> CollectorDtoList = db.findAllByWhere(
                    CollectorDto.class, " isDownLoad ==2 ", " date ");
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
                downLoadIntent.putExtra("title", CollectorDtoList.get(0)
                        .getTitle());
                downLoadIntent.putExtra("source", CollectorDtoList.get(0)
                        .getSource());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        context.startService(downLoadIntent);

                    }
                }, 200);

            }

        }
    }

}
