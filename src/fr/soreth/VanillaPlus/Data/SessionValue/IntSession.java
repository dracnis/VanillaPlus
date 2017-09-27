package fr.soreth.VanillaPlus.Data.SessionValue;

public class IntSession {
	private int base, current, last;
	private boolean set = false;
	public IntSession(int base){
		this.base = base;
		this.current = base;
		this.last = current;
	}
	public void set(int value){
		set = set || value != current;
		this.current = value;
	}
	public void setLast(int value){
		this.current = value + getChange();
		this.last = value;
	}
	public int get(){
		return this.current;
	}
	public boolean changed(){
		return this.current != this.last;
	}
	public int getChange(){
		return this.current - this.last;
	}
	public int getSession(){
		return this.current - this.base;
	}
	public void add(int d) {
		this.current += d;
	}
	public void save(){
		last = current;
	}
	public boolean set() {
		return set;
	}
	
}
