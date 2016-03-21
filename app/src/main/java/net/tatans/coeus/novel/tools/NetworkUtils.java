package net.tatans.coeus.novel.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/*public static String getResponseResult(String url) {
		String text = "";
		HttpURLConnection cumtConnection;
		try {
			cumtConnection = (HttpURLConnection) new URL(url).openConnection();
			cumtConnection
					.setRequestProperty("User-Agent",
							"ZhuiShuShenQi/3.35.1 (Android 5.1; LGE Hammerhead / Google Nexus 5");
			cumtConnection.setRequestProperty("Accept-Encoding", "gzip");
			InputStream urlStream = new GZIPInputStream(
					cumtConnection.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlStream, "UTF-8"));
			String line = "";

			while ((line = reader.readLine()) != null) {
				text = text + line;

			}
			reader.close();
			urlStream.close();
		} catch (MalformedURLException e) {
			text = "";
			e.printStackTrace();
		} catch (IOException e) {
			text = "";
			e.printStackTrace();
		}

		return text;
	}*/

}
