package data;
import java.util.Vector;

import display.ConsoleAdder;

public class Set extends Object{
	private String setCode;
	private String setName;
	private Vector<Card> cards;
	
	/**
	 * Creates a new set with the given setCode
	 * @param setCode the 3 letter code for the set
	 */
	public Set(String setCode){
		this.setSetCode(setCode);
		cards = new Vector<Card>();
		ConsoleAdder.getInstance().add("Created " + setCode);
	}
	public Vector<Card> getCards() { return cards; }
	public void setName(String setName) { this.setName = setName; }
	public String getName(){ return setName; }
	public String getCode(){ return getSetCode(); }
	public void addCard(Card c){
		if(cards.size() > 0){
			cards.get(cards.size() - 1).setNext(c);
		}
		cards.add(c); 
	}
	public String getSetCode() { return setCode; }
	public void setSetCode(String setCode) { this.setCode = setCode; }
	
	@Override
	public boolean equals(Object o){
		if(o == null){return false;}
		if(o instanceof Set){
			return setName.equals(((Set)o).setName) && setCode.equals(((Set)o).setCode);
		}
		return false;
	}
}