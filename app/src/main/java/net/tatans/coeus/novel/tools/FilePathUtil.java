package net.tatans.coeus.novel.tools;

import android.os.Environment;

/**
 * Created by Administrator on 2016/3/22.
 */
public class FilePathUtil {

    public static String getFilePath(String bookId, int chapaterNum, int sourceNum) {
        String Path = Environment.getExternalStorageDirectory()
                + "/tatans/novel/" + bookId + "/" + chapaterNum + "_" + sourceNum + ".txt";
        return Path;
    }
}
