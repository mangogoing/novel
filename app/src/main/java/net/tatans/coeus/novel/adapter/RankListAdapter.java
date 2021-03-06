package net.tatans.coeus.novel.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.activities.BookBriefActivity;
import net.tatans.coeus.novel.dto.RankListDto;

import java.util.List;

/**
 * 子排行榜adapter
 * 
 * @author shiyunfei
 * 
 */
public class RankListAdapter extends BaseAdapter {
	private List<RankListDto> al_BooksList;

	private Context context;

	private int page;

	@Override
	public int getCount() {
		return al_BooksList.size();
	}

	public RankListAdapter(List<RankListDto> list, Context context, int page) {
		super();
		this.al_BooksList = list;
		this.context = context;
		this.page = page;
	}

	@Override
	public Object getItem(int position) {
		return al_BooksList.get(position);
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
		RankListDto item = al_BooksList.get(position);
		final String id = item.get_id();
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
				+ "。按钮");
		ll_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, BookBriefActivity.class);
				intent.putExtra("_id", id);
				intent.putExtra("title", title);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
}
