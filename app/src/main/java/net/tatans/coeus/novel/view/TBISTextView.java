package net.tatans.coeus.novel.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * 自定义的view
 * @author luojianqin
 *
 */
public class TBISTextView extends TextView {

	private static String TAG = "TBISTextView";
	public TBISTextView(Context context) {
		super(context);
	}

	public TBISTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		return false;
	}

	@Override
	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
		return super.dispatchKeyShortcutEvent(event);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		resize();
	}
	
	/**
	 * 取出当前页无法显示的字
	 * @return 去掉的字数
	 */
	public int resize() {
		int resize = 0;
		try {
			CharSequence oldContent = getText();
			CharSequence newContent = oldContent.subSequence(0, getCharNum());
			setText(newContent);
			resize =  oldContent.length() - newContent.length();
		} catch (Exception e) {
			Log.e(TAG, e.getStackTrace()+"");
		}
		return resize;
		
	}
	/**
	 * 取出当前页显示的内容
	 * @return 当前页显示的内容
	 */
	public CharSequence reString() {
		CharSequence newContent = null;
		try {
			CharSequence oldContent = getText();
			newContent = oldContent.subSequence(0, getCharNum());
		} catch (Exception e) {
			Log.e(TAG, e.getStackTrace()+"");
		}
		return newContent;
		
	}
	
	/**
	 * 获取当前页总字数
	 */
	public int getCharNum() {
		try {
			return getLayout().getLineEnd(getLineNum());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 获取当前页总行数
	 */
	public int getLineNum() {
		int lineNum = 0;
		try {
			Layout layout = getLayout();
			int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
			lineNum = layout.getLineForVertical(topOfLastLine);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lineNum;
	}
}
