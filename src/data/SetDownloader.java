package data;

import java.io.File;
import java.util.ArrayList;

import display.ConsoleAdder;
import display.MainWindow;

public class SetDownloader implements Runnable, DownloadListener {
	private ArrayList<Card> cardsToDownload;
	private int currentCard;
	private String filePrefix;
	private String extension;
	private String directory;
	private MainWindow main;

	public SetDownloader(MainWindow main, ArrayList<Set> sets,
			String directory, String fileName, String extension) {
		this.main = main;
		this.filePrefix = fileName;
		this.directory = directory;
		this.extension = extension;
		cardsToDownload = new ArrayList<Card>();
		if (sets != null) {
			for (Set set : sets) {
				new File(directory + "/" +set.getCode()).mkdirs();
				for (Card card : set.getCards()) {
					card.setSet(set);
					cardsToDownload.add(card);
					
				}
			}
		}
		main.setProgBarMax(cardsToDownload.size());
		currentCard = 0;
	}

	private void downloadNextCard() {
		if (currentCard < cardsToDownload.size()) {
			Card card = cardsToDownload.get(currentCard);
			currentCard++;
			String local = filePrefix + extension;
			local = local.replaceAll("CARDNAME", card.toString());
			local = local.replaceAll("SETCODE", card.getSet().getCode());
			local = directory + "/" + card.getSet().getCode() + "/" + local;
			File save = new File(local);
			ImageDownloader downloader = new ImageDownloader(card.getId(), save,
						extension, this);
			new Thread(downloader).start();
			
		}
	}

	@Override
	public void fileSuccessfullyDownloaded(File f) {
		main.setProgressBarText(currentCard, "Downloaded " + currentCard + "/"
				+ cardsToDownload.size());
		ConsoleAdder.getInstance().add("Downloaded: " + f.getAbsolutePath());
		downloadNextCard();
	}

	@Override
	public void failedDownload(String s) {
		main.setProgressBarText(currentCard, "Downloaded " + currentCard + "/"
				+ cardsToDownload.size());
		ConsoleAdder.getInstance().addError(s);
		downloadNextCard();
	}

	@Override
	public void run() {
		downloadNextCard();
	}
}
