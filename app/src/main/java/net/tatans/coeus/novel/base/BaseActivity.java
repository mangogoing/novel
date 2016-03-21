package net.tatans.coeus.novel.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;

import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.novel.tools.GestureUtils;
import net.tatans.coeus.novel.tools.GestureUtils.Screen;

public abstract class BaseActivity extends Activity {

	private GestureDetector mGestureDetector;
	private Screen screen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGestureDetector = new GestureDetector(this, new myOnGestureListener());
		screen = GestureUtils.getScreenPix(this);
	}

	/**
	 * 手势按钮分发
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mGestureDetector != null) {
			if (mGestureDetector.onTouchEvent(ev))
				return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 切屏第一屏跟最后一屏时候快速切换时候可以快速播报
	 */
	private class myOnGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();
			// 限制必须得划过屏幕的1/4才能算划过
			float x_limit = screen.widthPixels / 4;
			float y_limit = screen.heightPixels / 4;
			float x_abs = Math.abs(x);
			float y_abs = Math.abs(y);
			if (x_abs >= y_abs) {
				// gesture left or right
				if (x > x_limit || x < -x_limit) {
					if (x > 0) {
						// right
						right();
						Log.e("SentenceSplitActivity", "mDetector 向右:");
					} else if (x <= 0) {
						// left
						left();
						Log.e("SentenceSplitActivity", "mDetector 向左:");

					}
				}
			} else {
				// gesture down or up
				if (y > y_limit || y < -y_limit) {
					if (y > 0) {
						// down
						down();
						Log.e("SentenceSplitActivity", "mDetector 向下:");
					} else if (y <= 0) {
						// up
						up();
						Log.e("SentenceSplitActivity", "mDetector 向上:");
					}
				}
			}
			return true;
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// Do something.
			try {
				Log.e("interruptTalkback", "0");
				AccessibilityManager accessibilityManager = (AccessibilityManager) getApplicationContext()
						.getSystemService(Context.ACCESSIBILITY_SERVICE);
				accessibilityManager.interrupt();
				TatansToast.cancel();
			} catch (Exception e) {
				Log.e("interruptTalkback", "未开启talkback");
			}
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public abstract void left();

	public abstract void right();

	public abstract void up();

	public abstract void down();

}
