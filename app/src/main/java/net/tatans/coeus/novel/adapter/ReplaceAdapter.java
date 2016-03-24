package net.tatans.coeus.novel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.tatans.coeus.novel.R;

import java.util.List;

/**
 * 子主题adapter
 *
 * @author shiyunfei
 */
public class ReplaceAdapter extends BaseAdapter {
    private Context context;
    private List<String> resourcesList;
    private List<String> isDownloadList;

    @Override
    public int getCount() {
        return resourcesList.size();
    }

    public ReplaceAdapter(Context context, List<String> resourcesList, List<String> isDownloadList) {
        this.context = context;
        this.resourcesList = resourcesList;
        this.isDownloadList = isDownloadList;
    }

    @Override
    public Object getItem(int position) {
        return resourcesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.replace_item, parent, false);
        }
        String resources = resourcesList.get(position);
//        String isDownload = isDownloadList.get(position);
        TextView resourcesView = (TextView) convertView
                .findViewById(R.id.tv_item_name);
        TextView isDownloadView = (TextView) convertView
                .findViewById(R.id.tv_is_download);
        resourcesView.setText(resources);
        resourcesView.setContentDescription(resources + "。");
//        isDownloadView.setText(isDownload);
//        isDownloadView.setContentDescription("已下载"+"。按钮");

        return convertView;
    }


}
