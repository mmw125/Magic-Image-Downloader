package data;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.stream.JsonReader;

import display.ConsoleAdder;
import display.MainWindow;
/**
 * This imports a json file containing all of the card data into card and set classes
 * This is relatively efficient and should take a second or two depending on the speed of the computer
 * @author Mark Wiggans
 *
 */
public class DataParser implements Runnable {
	private ArrayList<Card> cards;
	private ArrayList<Set> sets;
	private File dataFile;
	private ConsoleAdder consoleAdder;
	private MainWindow main;
	private final long WAIT_TIME = (long) 5000;
	
	public DataParser(ConsoleAdder adder, MainWindow mainWindow) {
		super();
		consoleAdder = adder;
		main = mainWindow;
		cards = new ArrayList<Card>();
		sets = new ArrayList<Set>();
		dataFile = new File("AllSets.json");
	}
	
	/**
	 * Parses a Json file with the magic set and card information
	 * @throws IOException
	 */
	public void parseData() throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(new File("AllSets.json")), "UTF-8"));
		try{
			readArray(reader);
		}finally{
			reader.close();
		}
	}
	
	/**
	 * 
	 * @param reader
	 * @throws IOException
	 */
	public void readArray(JsonReader reader) throws IOException{
		reader.beginObject();
		while(reader.hasNext()){
			sets.add(parseSet(reader));
		}
	}
	
	/**
	 * Reads and imports a set from the reader
	 * @param reader the reader to read from
	 * @return a set with all of the cards properly added
	 * @throws IOException
	 */
	public Set parseSet(JsonReader reader) throws IOException{
		String setName = reader.nextName();
		reader.beginObject();
		Set set = new Set(setName);
		while(reader.hasNext()){
			String name = reader.nextName();
			if(name.equals("name")){
				set.setName(reader.nextString());
			}else if(name.equals("cards")){
				reader.beginArray();
				while(reader.hasNext()){
					Card card = parseCard(reader);
					set.addCard(card);
					cards.add(card);
				}
				reader.endArray();
			}else{
				reader.skipValue();
			}
		}
		reader.endObject();
		return set;
	}
	
	/**
	 * Creates a card given a reader that is starting an object
	 * @param reader the reader to read from
	 * @return a card with the given quantities
	 * @throws IOException
	 */
	public Card parseCard(JsonReader reader) throws IOException{
		reader.beginObject();
		Card c = new Card();
		while(reader.hasNext()){
			String name = reader.nextName();
			if(name.equals("name")){
				c.setName(reader.nextString());
			}else if(name.equals("multiverseid")){
				c.setMultiverseId(reader.nextInt());
			}else{
				reader.skipValue();
			}
		}
		reader.endObject();
		return c;
	}
	
	/**
	 * Turns a set name into the code for that set
	 * @param setName the given set name
	 * @return the code for the given set. null if it doesn't exist
	 */
	public String setNameToCode(String setName){
		for(Set set : sets){
			if(set.getName().equals(setName)){
				return set.getSetCode();
			}
		}
		return null;
	}
	
	public Set setNameToSet(String setName){
		if(sets != null){
			for(Set set : sets){
				if(set != null && set.getName() != null && set.getName().equals(setName)){
					return set;
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets all of the cards in the file that it parsed
	 * @return
	 */
	public ArrayList<Card> getCards(){
		return cards;
	}
	
	public ArrayList<Set> getSets(){
		return sets;
	}
	
	private JsonReader getReader() throws Exception{
		//If dataFile does not exist, try to download it
		if (!dataFile.exists()){
			consoleAdder.add("allSets.json does not exist... downloading");
			URL url = new URL("http://mtgjson.com/json/AllSets.json");
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(dataFile);
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					ConsoleAdder.getInstance().add("...");
				}
			}, WAIT_TIME, WAIT_TIME);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			timer.cancel();
			consoleAdder.add("Download Successful");
			fos.close();
		}
		return new JsonReader(new InputStreamReader(new FileInputStream(dataFile), "UTF-8"));
	}
	
	private JsonReader redownloadReader(){
		dataFile.delete();
		try {
			return getReader();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void run() {
		JsonReader reader = null;
		try {
			reader = getReader();
		} catch (Exception e) {
			consoleAdder.addError(e.getMessage());
			e.printStackTrace();
		}
		try{
			if(reader != null){
				consoleAdder.add("Parsing data file");
				readArray(reader);
			}
		} catch (IOException e) {
			ConsoleAdder.getInstance().addError("Cannot read info file. Redownloading");
			reader = redownloadReader();
			if(reader != null){
				consoleAdder.add("Parsing data file");
				try {
					readArray(reader);
				} catch (Exception e1) {
					e1.printStackTrace();
					ConsoleAdder.getInstance().addError("Cannot read the redownloaded file. Exiting");
					return;
				}
			}
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		main.loadMain();
	}
}
