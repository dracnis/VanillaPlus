package fr.soreth.VanillaPlus.Data.SessionValue;

public class StringSession {
	private String base, current;
	public StringSession(String base){
		this.base = base;
		this.current = base;
	}
	public void set(String value){
		this.current = value;
	}
	public void load(String value){
		this.current = value;
		this.base = value;
	}
	public String get(){
		return this.current;
	}
	public boolean changed(){
		return !(current == base || (current != null && current.equals(base)));
	}
	public void save(){
		base = current;
	}	
}
