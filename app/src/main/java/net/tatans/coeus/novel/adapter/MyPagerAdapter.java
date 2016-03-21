package net.tatans.coeus.novel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;

import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.view.TBISTextView;

import java.util.ArrayList;
import java.util.List;

public class MyPagerAdapter extends PagerAdapter {

	private Context context;
	private List<View> viewContainter = new ArrayList<View>();
	private String data ="";
	private TBISTextView mEditText ;// 创建一个实例，不然会报异常;

	

	public MyPagerAdapter(Context context, List<View> viewContainter, String data) {
		this.context = context;
		this.viewContainter = viewContainter;
		this.data = data;
		mEditText = new TBISTextView(context);// 创建一个实例，不然会报异常;
	}

	@Override
	public int getCount() {
		return viewContainter.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@SuppressLint("NewApi")
	@Override
	public Object instantiateItem(View container, int position) {
        View v = (View) viewContainter.get(position);
		((ViewGroup) container).addView(v);
        mEditText = (TBISTextView)v.findViewById(R.id.edit_txt_play);
		mEditText.setCursorVisible(false); // 设置输入框中的光标不可见
		mEditText.setFocusable(false); // 无焦点
		mEditText.setFocusableInTouchMode(false); // 触摸时也得不到焦点
		mEditText.setClickable(false);
		// 为了防止触摸到文本框时有两个声音同时播报
		mEditText.setOnGenericMotionListener(new OnGenericMotionListener() {

			@Override
			public boolean onGenericMotion(View v, MotionEvent event) {
				interruptTalkback(context);
				Log.e("hhh", "onGenericMotion");
				return false;
			}
		});
		Log.d("MyPagerAdapter", "data:" +data);

		mEditText.setText(data);
//		Log.d("MyPagerAdapter", "textSize:" + mEditText.resize());

		return v;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(viewContainter.get(position));
	}
	
	/**
	 * 打断talkback
	 */
	public void interruptTalkback(Context context) {
		Log.e("hhh", "打断talkback");
		try {
			AccessibilityManager accessibilityManager = (AccessibilityManager) context
					.getSystemService(Context.ACCESSIBILITY_SERVICE);
			accessibilityManager.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getCharNum() {
		return mEditText.getCharNum();
	}

	public int resize() {
		return mEditText.resize();
	}

	public CharSequence  reString() {
		return mEditText.reString();
	}

	public CharSequence getText() {
		return mEditText.getText();

	}

}


