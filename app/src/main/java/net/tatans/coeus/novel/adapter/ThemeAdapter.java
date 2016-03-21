package net.tatans.coeus.novel.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.activities.ThemeListActivity;
import net.tatans.coeus.novel.dto.ThemeDto;

import java.util.List;
/**
 * 主题adapter
 * 
 * @author shiyunfei
 * 
 */
public class ThemeAdapter extends BaseAdapter{
	private List<ThemeDto> al_BooksTheme;

	private Context context;
	
	private int page;
	public ThemeAdapter(List<ThemeDto> list,
			Context context, int page) {
		super();
		this.al_BooksTheme = list;
		this.context = context;
		this.page = page;
	}

	@Override
	public int getCount() {
		return al_BooksTheme.size();
	}

	@Override
	public Object getItem(int position) {
		return al_BooksTheme.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
		}
		ThemeDto item = al_BooksTheme.get(position);
		final String id =item.get_id();
		final String title = item.getTitle();
		TextView textView =(TextView) convertView.findViewById(R.id.tv_item_name);
		textView.setText(title);
		textView.setContentDescription(title+"。按钮");
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ThemeListActivity.class);
				intent.putExtra("id", id);
				intent.putExtra("title", title);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
		return convertView;
	}

}
