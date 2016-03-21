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
 * 
 */
public class TitleAdapter extends BaseAdapter  {
	private Context context;
	private List<String> classifyList;

	@Override
	public int getCount() {
		return classifyList.size();
	}

	public TitleAdapter(Context context, List<String> list) {
		this.context = context;
		this.classifyList = list;

	}

	@Override
	public Object getItem(int position) {
		return classifyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item, parent, false);
		}
		String item = classifyList.get(position);

		TextView textView = (TextView) convertView
				.findViewById(R.id.tv_item_name);
		textView.setText(item);
		textView.setContentDescription(item + "。按钮");

		return convertView;
	}


}
