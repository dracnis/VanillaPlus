package fr.soreth.VanillaPlus.Data.SessionValue;

public class BooleanSession {
	private boolean base, current;
	public BooleanSession(boolean base){
		this.base = base;
		this.current = base;
	}
	public boolean set(boolean value){
		if( current == value ) return false;
		this.current = value;
		return true;
	}
	public boolean get(){
		return this.current;
	}
	public boolean changed(){
		return this.current != this.base;
	}
	public void save() {
		base = current;
	}
	
}
