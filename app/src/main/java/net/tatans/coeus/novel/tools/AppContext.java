package net.tatans.coeus.novel.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @description TODO
 * @author zcloud
 * @date Dec 8, 2013
 */
@SuppressWarnings("deprecation")
public class AppContext
{
	/**
	 * 网络是否连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return !(networkInfo == null || !networkInfo.isConnected());
	}

}
