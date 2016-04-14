package net.tatans.coeus.novel.tools;

import net.tatans.coeus.novel.dto.ChapterDto;
import net.tatans.coeus.novel.dto.SummaryDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

	private static List<SummaryDto> list;

	public static List<ChapterDto> getChapterList(String jsonResult) {
		List<ChapterDto> chapterList = new ArrayList<>();
		try {
			JSONObject json = new JSONObject(jsonResult.toString());
			JSONObject jsonMixToc = new JSONObject(json.getString("mixToc"));
			JSONArray chaptersArray = jsonMixToc.getJSONArray("chapters");
			for (int i = 0; i < chaptersArray.length(); i++) {
				ChapterDto chapterDto = new ChapterDto();
				String title = chaptersArray.getJSONObject(i)
						.getString("title");
				String link = chaptersArray.getJSONObject(i).getString("link");
				chapterDto.setLink(link);
				chapterDto.setTitle(title);
				chapterList.add(chapterDto);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return chapterList;

	}

	public static String getNovelContent(String jsonResult) {
		String result = "";
		try {
			JSONObject json = new JSONObject(jsonResult.toString());
			String ok = json.getString("ok");
			if (ok.equals("true")) {
				JSONObject jsonChapter = new JSONObject(
						json.getString("chapter"));
				String cpContent;
				if (jsonChapter.has("cpContent")) {
					cpContent = jsonChapter.getString("cpContent");
				} else {

					cpContent = jsonChapter.getString("body");
				}
				result = cpContent;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static List<SummaryDto> getSummaryListByJson(String result) {
		List<SummaryDto> list = new ArrayList<>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			SummaryDto summaryDto = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				summaryDto = new SummaryDto();
				JSONObject item = jsonArray.getJSONObject(i); // 得到每个对象
				summaryDto.set_id(item.getString("_id"));
				summaryDto.setLastChapter(item.getString("lastChapter"));
				summaryDto.setLink(item.getString("link"));
				summaryDto.setName(item.getString("name"));
				summaryDto.setSource(item.getString("source"));
				summaryDto.setCharge(item.getBoolean("isCharge"));
				summaryDto.setChaptersCount(item.getInt("chaptersCount"));
				summaryDto.setUpdated(item.getString("updated"));
				summaryDto.setStarting(item.getBoolean("starting"));
				summaryDto.setHost(item.getString("host"));
				list.add(summaryDto);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<ChapterDto> getChapterListByJson(String result) {
		List<ChapterDto> list = new ArrayList<>();

		try {
			JSONObject jObject = new JSONObject(result);
			JSONArray jsonArray = jObject.getJSONArray("chapters");
			ChapterDto chapterDto = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				chapterDto = new ChapterDto();
				chapterDto.setTitle(jsonArray.getJSONObject(i).getString(
						"title"));
				chapterDto
						.setLink(jsonArray.getJSONObject(i).getString("link"));
				list.add(chapterDto);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<String> getChapterNameListByJson(String result) {
		List<String> list = new ArrayList<>();

		try {
			JSONObject jObject = new JSONObject(result);
			JSONArray jsonArray = jObject.getJSONArray("chapters");
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(jsonArray.getJSONObject(i).getString("title"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

}
