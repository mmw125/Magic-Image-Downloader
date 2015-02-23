package display;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ConsoleAdder implements Runnable {
	private JPanel console;
	private JLabel toAdd;
	private static ConsoleAdder instance;

	ConsoleAdder(JPanel console) {
		this.console = console;
		instance = this;
	}
	
	public static ConsoleAdder getInstance(){
		return instance;
	}

	public void add(String string) {
		toAdd = new JLabel(string);
		SwingUtilities.invokeLater(this);
	}
	
	public void addError(String error){
		JLabel lab = new JLabel();
		lab.setForeground(Color.RED);
		toAdd = lab;
		SwingUtilities.invokeLater(this);
	}

	@Override
	public void run() {
		console.add(toAdd);
		console.revalidate();
	}

}
