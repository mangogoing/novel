package net.tatans.coeus.novel.tools;

import android.util.Log;

import net.tatans.coeus.network.utils.DirPath;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件读写删除---工具类
 * 
 * @author shiyunfei
 * 
 */
public class FileUtil {

	// 读取txt文件内容
	public static StringBuffer read(String filePath) throws IOException {
		BufferedReader br = null;
		StringBuffer sb = null;
		try {
			Log.d("WWWWWWWWW", getCharset(filePath));
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					filePath), getCharset(filePath));
			br = new BufferedReader(isr);
			sb = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp + "\r\n");
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb;
	}

	@SuppressWarnings("resource")
	private static String getCharset(String fileName) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(
				fileName));
		int p = (bin.read() << 8) + bin.read();

		String code = null;

		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}
		return code;
	}

	// /**
	// * 根据需求,直接调用静态方法start来执行操作 参数: rows 为多少行一个文件 int 类型 sourceFilePath 为源文件路径
	// * String 类型 targetDirectoryPath 为文件分割后存放的目标目录 String 类型
	// * ---分割后的文件名为索引号(从0开始)加'_'加源文件名,例如源文件名为test.txt,则分割后文件名为0_test.txt,以此类推
	// */
//	public static int divide(int rows, String sourceFilePath,
//			String targetDirectoryPath) {
//		int count = 0;
//		File sourceFile = new File(sourceFilePath);
//		File targetFile = new File(targetDirectoryPath);
//		if (!sourceFile.exists() || rows <= 0 || sourceFile.isDirectory()) {
//			System.out.println("源文件不存在或者输入了错误的行数");
//			return count;
//		}
//		if (targetFile.exists()) {
//			if (!targetFile.isDirectory()) {
//				System.out.println("目标文件夹错误,不是一个文件夹");
//				return count;
//			}
//		} else {
//			targetFile.mkdirs();
//		}
//		try {
//			InputStreamReader isr = new InputStreamReader(new FileInputStream(
//					sourceFilePath), getCharset(sourceFilePath));
//			BufferedReader br = new BufferedReader(isr);
//			BufferedWriter bw = null;
//			String str = "";
//			String tempData = br.readLine();
//			int i = 1, s = 0;
//			while (tempData != null) {
//				str += tempData + " " + "\r\n";
//				if (i % rows == 0) {
//					OutputStreamWriter write = new OutputStreamWriter(
//							new FileOutputStream(new File(
//									targetFile.getAbsolutePath() + "/" + s
//											+ ".txt")), "GBK");
//					BufferedWriter writer = new BufferedWriter(write);
//
//					// bw = new BufferedWriter(new FileWriter(new File(
//					// targetFile.getAbsolutePath() + "/" + s + "_"
//					// + sourceFile.getName())));
//					writer.write(str);
//					writer.close();
//					str = "";
//					s += 1;
//				}
//				i++;
//				tempData = br.readLine();
//			}
//			if ((i - 1) % rows != 0) {
//				// bw = new BufferedWriter(new FileWriter(new File(
//				// targetFile.getAbsolutePath() + "/" + s + "_"
//				// + sourceFile.getName())));
//				OutputStreamWriter write = new OutputStreamWriter(
//						new FileOutputStream(
//								new File(targetFile.getAbsolutePath() + "/" + s
//										+ ".txt")), "GBK");
//				BufferedWriter writer = new BufferedWriter(write);
//				// PrintWriter writer = new PrintWriter(new BufferedWriter(new
//				// FileWriter(filePathAndName)));
//				// PrintWriter writer = new PrintWriter(new
//				// FileWriter(filePathAndName));
//				writer.write(str);
//				writer.close();
//				br.close();
//				s += 1;
//			}
//			count = s;
//			System.out.println("文件分割结束,共分割成了" + s + "个文件");
//		} catch (Exception e) {
//		}
//		return count;
//	}

	// 判断文件是否存在
	public static boolean fileIsExists(String filePath) {
		try {
			File f = new File(filePath);
			if (!f.exists()) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			return false;
		}
	}

	// 删除文件或文件夹
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	// 创建文件夹并写入数据
	public static void write(String chapterContent, int i, String _id,int sourceNum) {
		// 创建路径和空的.TXT文件
		String filenameTemp = DirPath.getMyCacheDir("novel/" + _id, i +  "_" + sourceNum +".txt");
		BufferedWriter writer = null;
		try {
			File f = new File(filenameTemp);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(f), "GBK");
			writer = new BufferedWriter(write);
			writer.write(chapterContent);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int divide(String srcFilePath, String novelName, String dstDir) {

		BufferedWriter bw = null;
		int i = 0;
		int index = 0;
		File sourceFile = new File(srcFilePath);
		File targetFile = new File(dstDir);
		if (!sourceFile.exists() || sourceFile.isDirectory()) {
			System.out.println("源文件不存在或者输入了错误的行数");
			return i;
		}
		if (targetFile.exists()) {
			if (!targetFile.isDirectory()) {
				System.out.println("目标文件夹错误,不是一个文件夹");
				return i;
			}
		} else {
			targetFile.mkdirs();
		}
		try {
			StringBuffer sb = new StringBuffer();
			StringBuffer sbCatalog = new StringBuffer();
			sbCatalog.append(novelName+ "\r\n");
			Pattern p = Pattern
					.compile("第+[0-9一二三四五六七八九十百千万亿壹贰叁肆伍陆柒捌玖拾佰仟]{1,9}+[章节卷集部篇回]");
			Matcher m = null;
			File file;
			sb = read(srcFilePath);
			m = p.matcher(sb);
			while (m.find()) {
				System.out.println(m.group(0));
				sbCatalog.append(m.group(0) +"\r\n");
				file = new File(targetFile.getAbsolutePath() + "/" + i + ".txt");
				OutputStreamWriter write = new OutputStreamWriter(
						new FileOutputStream(file), "GBK");
				bw = new BufferedWriter(write);
				bw.write(sb.toString(), index, (m.start() - index));
				bw.flush();
				bw.close();
				index = m.start();
				i++;
			}

			file = new File(targetFile.getAbsolutePath() + "/" + -1 + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStreamWriter writeCatalog = new OutputStreamWriter(
					new FileOutputStream(file), "GBK");
			bw = new BufferedWriter(writeCatalog);
			bw.write(sbCatalog.toString());
			bw.flush();
			bw.close();

			file = new File(targetFile.getAbsolutePath() + "/" + i + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(file), "GBK");
			bw = new BufferedWriter(write);
			bw.write(sb.toString(), index, sb.length() - index);
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return i+1;
	}

	// 读取txt文件内容
	public static List<String> read2Chapter(String filePath) throws IOException {
		List<String> chapterList = new ArrayList<>();
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					filePath), getCharset(filePath));
			BufferedReader br = new BufferedReader(isr);
			String temp = null;
			while ((temp = br.readLine()) != null) {
				if (isContainChinese(temp)) {
					chapterList.add(temp.replace(" ", ""));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return chapterList;
	}

	public static boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

}
