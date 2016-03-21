package net.tatans.coeus.novel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.dto.BookListDto;

import java.util.List;

/**
 * 子主题adapter
 * 
 * @author shiyunfei
 * 
 */
public class BookListAdapter extends BaseAdapter {
	private Context context;
	private List<BookListDto> classifyList;

	@Override
	public int getCount() {
		return classifyList.size();
	}

	public BookListAdapter(Context context, List<BookListDto> classificatList) {
		this.context = context;
		this.classifyList = classificatList;

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
					R.layout.book_list_item, parent, false);
		}
		BookListDto item = classifyList.get(position);
		final String title = item.getTitle();
		String contentDescription1 = title + ",作者：" + item.getAuthor() + "。";
		String contentDescription2 = item.getLatelyFollower() + "人在追,"
				+ item.getRetentionRatio() + "%的读者留存。";
		// String text = contentDescription.substring(0, 18) + "....";
		LinearLayout ll_item = (LinearLayout) convertView
				.findViewById(R.id.ll_item);
		TextView tv_item_name = (TextView) convertView
				.findViewById(R.id.tv_item_name);
		TextView tv_item_statistic = (TextView) convertView
				.findViewById(R.id.tv_item_statistic);
		tv_item_name.setText(contentDescription1);
		tv_item_statistic.setText(contentDescription2);
		ll_item.setContentDescription(contentDescription1 + contentDescription2
				+ "按钮");
		return convertView;
	}

}
