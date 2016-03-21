package net.tatans.coeus.novel.tools;

import net.tatans.coeus.novel.dto.SourcesDto;

import java.util.ArrayList;

public class NovelUtil {

	public static String getSourceId(ArrayList<SourcesDto> sourcesList) {
		String source = "";
		String sourceId = "";
		if (sourcesList.size() > 0) {
			source = sourcesList.get(0).getSource();
			if (source.equals("easou")) {
				String idEasou[] = sourcesList.get(0).getSourceId().split(":");
				String gid = idEasou[0];
				String nid = idEasou[1];
				sourceId = "gid=" + gid + "&nid=" + nid;
			} else if (source.equals("leidian")) {
				String leidianSourceId = sourcesList.get(0).getSourceId();
				sourceId = leidianSourceId;
			}
		}
		return sourceId;

	}

	public static String getSource(ArrayList<SourcesDto> sourcesList) {
		String source = "";
		if (sourcesList.size() > 0) {
			source = sourcesList.get(0).getSource();
		}
		return source;

	}

	public static int getChapterCount(ArrayList<SourcesDto> sourcesList) {
		int chapterCount = 0;
		if (sourcesList.size() > 0) {
			chapterCount = sourcesList.get(0).getChapterCount();
		}
		return chapterCount;

	}

}
