package net.tatans.coeus.novel.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import net.tatans.coeus.novel.R;
import net.tatans.coeus.novel.base.BaseActivity;

public class DisclaimerActivity extends BaseActivity {
	TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("免责声明");
		setContentView(R.layout.disclaimer);
		textView=(TextView) findViewById(R.id.tv_disclaimer_body);
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
	@Override
	public void left() {
		
	}
	@Override
	public void right() {
		
	}
	@Override
	public void up() {
		
	}
	@Override
	public void down() {
		
	}
}
