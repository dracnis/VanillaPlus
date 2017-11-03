package fr.soreth.VanillaPlus.Data.SQLite;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Data.Column;
import fr.soreth.VanillaPlus.Data.IResultQuery;

public class SQLiteResult implements IResultQuery{
	private ResultSet result;
	private boolean closed;
	public SQLiteResult(ResultSet result){
		this.result = result;
	}
	
	@Override
	public Object get(Column column){
		try {
			return result.getObject(column.getId());
		} catch (SQLException e) {
			Error.SQL.add();
			e.printStackTrace();
		}
		return column.getDefaultValue();
	}
	@Override
	public String getString(Column column) {
		try {
			return result.getString(column.getId());
		} catch (SQLException e) {
			Error.SQL.add();
			e.printStackTrace();
		}
		return (String) column.getDefaultValue();
	}
	@Override
	public Integer getInt(Column column) {
		try {
			return result.getInt(column.getId());
		} catch (SQLException e) {
			Error.SQL.add();
			e.printStackTrace();
		}
		return (Integer) column.getDefaultValue();
	}
	@Override
	public Double getDouble(Column column) {
		try {
			return result.getDouble(column.getId());
		} catch (SQLException e) {
			Error.SQL.add();
			e.printStackTrace();
		}
		return (Double) column.getDefaultValue();
	}
	@Override
	public boolean next() {
		try {
			return result.next();
		} catch (SQLException e) {
			Error.SQL.add();
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public void close() {
		try {
			if(!closed)
				result.close();
		} catch (SQLException e) {
			Error.SQL.add();
			e.printStackTrace();
		}
	}
}
