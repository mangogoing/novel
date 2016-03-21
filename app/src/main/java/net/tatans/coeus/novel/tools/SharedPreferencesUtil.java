package net.tatans.coeus.novel.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

	public static void saveData(Context context, int sourceNum) {
		// 实例化SharedPreferences对象（第一步）
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				"novel_sp", Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putInt("sourceNum", sourceNum);
		// 提交当前数据
		editor.commit();

	}

	public static int readData(Context context) {
		SharedPreferences sp = context.getSharedPreferences("novel_sp",
				Activity.MODE_PRIVATE);
		return sp.getInt("sourceNum", 0);
	}

}
