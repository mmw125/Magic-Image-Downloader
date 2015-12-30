package data;
import java.io.File;

public interface DownloadListener {
	public void fileSuccessfullyDownloaded(File f);
	public void failedDownload(String s);
}
