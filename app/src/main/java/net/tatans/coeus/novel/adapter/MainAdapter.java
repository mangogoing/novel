package net.tatans.coeus.novel.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.coeus.novel.R;

import java.util.List;

public class MainAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Bitmap search_icon;
	private Bitmap bookshelf_icon;
	private Bitmap rank_icon;
	private Bitmap theme_hot_icon;
	private Bitmap theme_new_icon;
	private Bitmap theme_collect_icon;
	private Bitmap male_icon;
	private Bitmap female_icon;
	private List<String> title;

	public MainAdapter(Context context, List<String> title) {
		mInflater = LayoutInflater.from(context);
		this.title = title;
		search_icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.search);
		bookshelf_icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bookshelf);
		rank_icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.rank);
		theme_hot_icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.theme_hot);
		theme_new_icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.theme_new);
		theme_collect_icon = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.theme_collect);
		male_icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.male);
		female_icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.female);
	}

	public int getCount() {
		return title.size();
	}

	public Object getItem(int position) {
		return title.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.main_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView
					.findViewById(R.id.tv_item_name);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		switch (title.get(position)) {
		case "搜索":
			holder.text.setText("搜索");
			holder.icon.setImageBitmap(search_icon);
			break;
		case "我的书架":
			holder.text.setText("我的书架");
			holder.icon.setImageBitmap(bookshelf_icon);
			break;
		case "排行榜":
			holder.text.setText("排行榜");
			holder.icon.setImageBitmap(rank_icon);
			break;
		case "本周最热主题":
			holder.text.setText("本周最热主题");
			holder.icon.setImageBitmap(theme_hot_icon);
			break;
		case "最新发布主题":
			holder.text.setText("最新发布主题");
			holder.icon.setImageBitmap(theme_new_icon);
			break;
		case "最多收藏主题":
			holder.text.setText("最多收藏主题");
			holder.icon.setImageBitmap(theme_collect_icon);
			break;
		case "男生分类":
			holder.text.setText("男生分类");
			holder.icon.setImageBitmap(male_icon);
			break;
		case "女生分类":
			holder.text.setText("女生分类");
			holder.icon.setImageBitmap(female_icon);
			break;

		default:
			break;
		}

		return convertView;
	}

	private class ViewHolder {
		TextView text;
		ImageView icon;
	}

}
