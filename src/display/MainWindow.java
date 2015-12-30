package display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import data.DataParser;
import data.Set;
import data.SetDownloader;

public class MainWindow implements ActionListener {
	private JFrame frame;
	private DataParser parser;
	private ArrayList<JCheckBox> setCheckBoxes;
	private ConsoleAdder adder;
	private JButton download;
	private File saveDirectory;
	private JLabel currentPathDisplay;
	private JPanel south;
	private JLabel fileNameDisplay;
	private JTextField fileName;
	private ButtonGroup fileExtension;
	private JProgressBar progBar;

	public MainWindow() throws IOException {
		loadConsole();
	}

	public static void main(String[] args) {
		try {
			new MainWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadConsole() {
		frame = new JFrame("Console");
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JScrollPane setHolder = new JScrollPane();
		frame.getContentPane().add(setHolder, BorderLayout.CENTER);

		JPanel console = new JPanel();
		console.setLayout(new BoxLayout(console, BoxLayout.Y_AXIS));
		console.setBackground(Color.WHITE);
		setHolder.setViewportView(console);

		adder = new ConsoleAdder(console);
		adder.add("Created ConsoleAdder");

		frame.revalidate();

		parser = new DataParser(adder, this);
		Thread t = new Thread(parser);
		t.start();
	}

	public void loadMain() {
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame = new JFrame();
		frame.setTitle("Magic Image Downloader");
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		frame.setSize(400, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		saveDirectory = new File("");

		addSetHolder();

		south = new JPanel();
		frame.getContentPane().add(south, BorderLayout.SOUTH);
		south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

		addPathSelector();

		addExtensionSelectors();

		fileName = new JTextField();
		south.add(fileName);
		fileName.setText("CARDNAME.full");
		fileName.setToolTipText("CARDNAME represents the card's english title. SETCODE represents the 3 letter set code");
		fileName.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				updateFileNameDisplay();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				updateFileNameDisplay();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				updateFileNameDisplay();
			}
		});

		fileNameDisplay = new JLabel();
		south.add(fileNameDisplay);
		fileNameDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);

		download = new JButton("Download");
		south.add(download);
		download.setAlignmentX(Component.CENTER_ALIGNMENT);
		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadSets();
			}
		});
		updateFileNameDisplay();
		updateButtonClickable();
		updatePathDisplay();
		frame.revalidate();
	}

	public void addSetHolder() {
		JScrollPane setHolder = new JScrollPane();
		frame.getContentPane().add(setHolder, BorderLayout.CENTER);
		JPanel setsDisplay = new JPanel();
		setsDisplay.setLayout(new BoxLayout(setsDisplay, BoxLayout.Y_AXIS));
		setHolder.setViewportView(setsDisplay);
		setCheckBoxes = new ArrayList<JCheckBox>();
		for (Set set : parser.getSets()) {
			JCheckBox setCheck = new JCheckBox(set.getName());
			setCheck.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateButtonClickable();
				}
			});
			setsDisplay.add(setCheck);
			setCheckBoxes.add(setCheck);
		}
	}

	public void addPathSelector() {
		JPanel destSelHol = new JPanel();
		destSelHol.setLayout(new BorderLayout());
		south.add(destSelHol);

		currentPathDisplay = new JLabel(saveDirectory.getAbsolutePath());
		destSelHol.add(currentPathDisplay, BorderLayout.CENTER);

		JButton selectPath = new JButton("Select Path");
		destSelHol.add(selectPath, BorderLayout.EAST);
		selectPath.addActionListener(this);
	}

	public void addExtensionSelectors() {
		JPanel radioHolder = new JPanel();
		radioHolder.setLayout(new BoxLayout(radioHolder, BoxLayout.X_AXIS));
		south.add(radioHolder);
		fileExtension = new ButtonGroup();
		JRadioButton rad = new JRadioButton(".jpg");
		rad.setSelected(true);
		fileExtension.add(rad);
		radioHolder.add(rad);
		rad = new JRadioButton(".png");
		fileExtension.add(rad);
		radioHolder.add(rad);
		rad = new JRadioButton(".bmp");
		fileExtension.add(rad);
		radioHolder.add(rad);
		rad = new JRadioButton(".wbmp");
		fileExtension.add(rad);
		radioHolder.add(rad);
		rad = new JRadioButton(".gif");
		fileExtension.add(rad);
		radioHolder.add(rad);
	}

	/**
	 * Checks if it can download the images
	 */
	public void updateButtonClickable() {
		for (JCheckBox check : setCheckBoxes) {
			if (check.isSelected()) {
				download.setEnabled(true);
				return;
			}
		}
		download.setEnabled(false);
		download.setToolTipText("Select at least one set to download");
	}

	public String getSelectedButton(){
		Enumeration<AbstractButton> buttons = fileExtension.getElements();
		while (buttons.hasMoreElements()) {
			AbstractButton button = buttons.nextElement();
			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}
	
	public void downloadSets() {
		ArrayList<Set> setsToDownload = new ArrayList<Set>();
		for (JCheckBox check : setCheckBoxes) {
			if (check != null && check.isSelected()) {
				Set set = parser.setNameToSet(check.getText());
				if (set != null) {
					setsToDownload.add(set);
				}
			}
		}
		south.remove(download);
		progBar = new JProgressBar();
		south.add(progBar);
		frame.revalidate();
		SetDownloader downloader = new SetDownloader(this, setsToDownload, saveDirectory.getAbsolutePath(), fileName.getText(), getSelectedButton());
		new Thread(downloader).start();
	}
	
	public void deselectAllSets(){
		for (JCheckBox check : setCheckBoxes) {
			if (check != null){
				check.setSelected(false);
			}
		}
	}

	public void updatePathDisplay() {
		currentPathDisplay.setText(saveDirectory.getAbsolutePath());
	}



	public void updateFileNameDisplay() {
		String str = fileName.getText();
		str = str.replaceAll("CARDNAME", parser.getCards().get(0).toString());
		str = str.replaceAll("SETCODE", parser.getSets().get(0).getCode());
		Enumeration<AbstractButton> buttons = fileExtension.getElements();
		while (buttons.hasMoreElements()) {
			AbstractButton button = buttons.nextElement();
			if (button.isSelected()) {
				str = str + button.getText();
				break;
			}
		}
		fileNameDisplay.setText(str);
	}
	
	public void setProgBarMax(int max){
		if(progBar != null){
			progBar.setMaximum(max);
		}
	}
	
	public void setProgressBarText(int progress, String display){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(progBar != null){
					progBar.setValue(progress);
					progBar.setString(display);
				}
				if(progress >= progBar.getMaximum()){
					south.remove(progBar);
					south.add(download);
					adder.add("Finished downloading");
					deselectAllSets();
				}
				frame.revalidate();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(saveDirectory);
		chooser.setDialogTitle("Select destination folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			adder.add("Chosen Directory: " + chooser.getSelectedFile());
			saveDirectory = chooser.getSelectedFile();
		} else {
			adder.add("No selection");
		}
		updatePathDisplay();
	}
}
