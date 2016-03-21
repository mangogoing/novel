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

import java.io.File;
import java.util.List;

public class MyAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Bitmap mIcon1;
	private Bitmap mIcon2;
	private Bitmap mIcon3;
	private Bitmap mIcon4;
	private Bitmap mIcon5;
	private Bitmap mIcon6;
	private List<String> items;
	private List<String> paths;

	public MyAdapter(Context context, List<String> it, List<String> pa) {
		mInflater = LayoutInflater.from(context);
		items = it;
		paths = pa;
		mIcon1 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.back01);
		mIcon2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.back02);
		mIcon3 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.folder);
		mIcon4 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.txt_icon);
		mIcon5 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.phone_storage);
		mIcon6 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sdcard);
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.file_row, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		File f = new File(paths.get(position).toString());
		if (items.get(position).toString().equals("b1")) {
			holder.text.setText("返回根目录..");
			holder.icon.setImageBitmap(mIcon1);
		} else if (items.get(position).toString().equals("b2")) {
			holder.text.setText("返回上一层..");
			holder.icon.setImageBitmap(mIcon2);
		} else {
			String name = f.getName();
			if (name.equals("sdcard0")) {
				name = "内部存储";
				holder.icon.setImageBitmap(mIcon5);
			} else if (name.equals("sdcard1")) {
				name = "sd卡";
				holder.icon.setImageBitmap(mIcon6);
			} else {

				if (f.isDirectory()) {
					holder.icon.setImageBitmap(mIcon3);
					holder.icon.setContentDescription("文件夹");
				} else {
					holder.icon.setContentDescription("文件");
					holder.icon.setImageBitmap(mIcon4);
				}
			}
			holder.text.setText(name);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView text;
		ImageView icon;
	}

}
