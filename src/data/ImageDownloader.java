package data;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageDownloader implements Runnable{
	private int id;
	private File file;
	private DownloadListener listener;
	private String extension;
	
	public ImageDownloader(int multiverseId, File saveLocation, String extension, DownloadListener listener){
		id = multiverseId;
		file = saveLocation;
		this.listener = listener;
		this.extension = extension;
	}
	
	private void downloadFile() throws Exception{
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + id + "&type=card");
		file.createNewFile();
		BufferedImage image = ImageIO.read(url);
		ImageIO.write(image, extension.substring(1), file);
	}

	@Override
	public void run() {
		try {
			downloadFile();
		} catch (Exception e) {
			listener.failedDownload(e.getMessage());
			e.printStackTrace();
			file.delete();
			return;
		}
		listener.fileSuccessfullyDownloaded(file);
	}
	
}
