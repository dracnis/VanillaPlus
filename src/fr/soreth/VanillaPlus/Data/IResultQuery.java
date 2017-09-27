package fr.soreth.VanillaPlus.Data;

public interface IResultQuery {
	public Object get(Column column);
	public String getString(Column column);
	public Integer getInt(Column column);
	public boolean next();
	public void close();
	public Double getDouble(Column column);
}
