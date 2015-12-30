package data;

public class Card{
	private String name;
	private Set set;
	private int multiverseId;
	private int cardNumber = -1;
	public Card(String cardName){
		name = cardName;
	}
	public Card() {	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public void setSet(Set s){
		set = s;
	}
	public Set getSet(){
		return set;
	}
	public void setMultiverseId(int d) {
		multiverseId = d;
	}
	public int getId(){
		return multiverseId;
	}
	public void setCardNumber(int number){
		cardNumber = number;
	}
	public void setNext(Card c){
		if(c.name.equals(name)){
			if(cardNumber == -1){
				cardNumber = 1;
			}
			c.cardNumber = cardNumber + 1;
		}
	}
	public String toString(){
		if(cardNumber == -1){
			return name;
		}else{
			return name+cardNumber;
		}
	}
}
