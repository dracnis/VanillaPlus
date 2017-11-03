package fr.soreth.VanillaPlus.Data.SessionValue;

public class DoubleSession {
	private double base, current, last;
	private boolean set = false;
	public DoubleSession(double base){
		this.base = base;
		this.current = base;
		this.last = current;
	}
	public boolean set(double value){
		if(value == current)return false;
		set = true;
		this.current = value;
		return true;
	}
	public void setLast(double value){
		this.current = value + getChange();
		this.last = value;
	}
	public double get(){
		return this.current;
	}
	public boolean changed(){
		return this.current != this.last;
	}
	public double getChange(){
		return this.current - this.last;
	}
	public double getSession(){
		return this.current - this.base;
	}
	public void add(double d) {
		this.current += d;
	}
	public void save(){
		last = current;
	}
	public boolean set() {
		return set;
	}
}
