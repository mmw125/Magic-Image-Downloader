package data;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
		URL url = new URL("http://mtgimage.com/multiverseid/" + id + ".jpg");
		file.createNewFile();
		BufferedImage image = ImageIO.read(url);
//		image = image.getSubimage(7, 7, image.getWidth() - 14, image.getHeight() - 15);
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
