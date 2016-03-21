package net.tatans.coeus.novel.activities;

public interface IUpdateDisplayState {
	public void UpdateDisplayState(boolean isSpeaking);

	public void UpdateViewShow(int currentPosition, String strShow,
							   int totalChapterCount);
}
