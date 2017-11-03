package fr.soreth.VanillaPlus.Data.SQLite;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Data.Column;
import fr.soreth.VanillaPlus.Data.Column.Type;
import fr.soreth.VanillaPlus.Data.IResultQuery;
import fr.soreth.VanillaPlus.Data.Table;

public class SQLiteTable extends Table{
	private Connection connection;
	private static final boolean weak = VanillaPlusCore.getBukkitVersionID() < 101200;
	public SQLiteTable(SQLiteConnection iConnection, Connection connection, String table) {
		super(iConnection, table);
		this.connection = connection;
    }
    public Select select() {
        return new Select(connection, getTableName());
    }
    protected Alter alter() {
		return new Alter(connection, getTableName());
	}
    protected Create create() {
        return new Create(connection, getTableName());
    }

    public Update update() {
        return new Update(connection, getTableName());
    }

    public Insert insert() {
        return new Insert(connection, getTableName());
    }

    public Delete delete() {
        return new Delete(connection, getTableName());
    }

	@Override
	public Table validate() {
		create().add(columns).execute();
		checkColumns(columns);
		return this;
	}
	private void checkColumns(List<Column> col) {
	    try {
		    DatabaseMetaData metadata = connection.getMetaData();
			ResultSet resultSet = metadata.getColumns(null, null, getTableName(), null);
			List<Column>missing = new ArrayList<Column>(col);
			while(resultSet.next()) {
				String name = resultSet.getString("COLUMN_NAME");
				Column current = getColumn(missing, name);
				if(current == null)continue;
				int id = resultSet.getInt("ORDINAL_POSITION");
				if(weak)id++;
				current.setId(id);
				missing.remove(current);
				String type = resultSet.getString("TYPE_NAME");/*
				if(current.getType() == Type.DOUBLE) {
					Integer size = resultSet.getInt("COLUMN_SIZE");
					Integer decimal = resultSet.getInt("DECIMAL_DIGITS");
					if(size != null && size != 0 && decimal != null)
						type += ("(" + size + "," + decimal + ")");
				}*/
				//System.out.println(resultSet.getString("COLUMN_NAME")+ " => " + type + " :: " + resultSet.getInt("ORDINAL_POSITION"));
				//System.out.println(type + "  " + typeToString(current.getType()));
				if(!type.equalsIgnoreCase(typeToString(current.getType())))
					ErrorLogger.addError("SQLite ALTER TABLE don't exist, can't edit " + current.getName());
			}
			if(!missing.isEmpty()) {
				Alter a = alter();
				for(Column c : missing)
					a.add(c);
				a.execute();
				//if(!missing.isEmpty())
					//checkColumns(missing);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class SQLiteQuery{
		protected Connection connection;
		protected Statement prest;
		protected String sql;
		public SQLiteQuery(Connection connection, String sql) {
			this.connection = connection;
			this.sql = sql;
		}
		public void close() {
			try {
				prest.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	}
	public class Select extends SQLiteQuery implements Table.ISelectQuery{
	    private boolean and; 
	    private final List<Object> values;
	    public Select(Connection connection, String table) {
	    	super(connection, "SELECT * FROM " + table);
	        and = false;
	        values = new ArrayList<Object>();
	    }
	    public Select where(Column column, Object value) {
            sql += and ? " AND" : " WHERE";
	        sql += " " + column.getName() + "= ?";
	        values.add(value);
	        and = true;
	        return this;
	    }
	    public IResultQuery execute() {
	        try {
	            prest = connection.prepareStatement(sql);
	            int i = 1;
	            for (Object object : values) {
	                ((PreparedStatement) prest).setObject(i, object);

	                i++;
	            }
	            return new SQLiteResult(((PreparedStatement) prest).executeQuery());
	        } catch (SQLException e) {
	    		System.out.println(prest.toString());
	            e.printStackTrace();
	            return null;
	        }
	    }
	}
	public class Alter extends SQLiteQuery implements Table.IAlterQuery{
		private boolean firstValue = true;
		public Alter(Connection connection, String table) {
			super(connection, "ALTER TABLE "+ table);
		}
		public IAlterQuery add(Column column) {
			if (!firstValue) {
				sql = sql.substring(0, sql.length() - 1) + ", ";
			} else {
				firstValue = false;
			}
			sql += " ADD COLUMN " + SQLiteTable.toString(column) + ";";
			return this;
		}
		public IAlterQuery remove(Column column) {
			if (!firstValue) {
				sql = sql.substring(0, sql.length() - 1) + ", ";
			} else {
				firstValue = false;
			}
			sql += " DROP COLUMN `" + column.getName() + ";";
			return this;
		}
		public IAlterQuery modify(Column column) {
			if (!firstValue) {
				sql = sql.substring(0, sql.length() - 1) + ", ";
			} else {
				firstValue = false;
			}
			sql += " MODIFY COLUMN " + SQLiteTable.toString(column) + ";";
			return this;
		}
		public IAlterQuery execute() {
			if(firstValue)return this;
			try {
				prest = connection.createStatement();
				prest.execute(sql);
				close();
			} catch (SQLException e) {
	    		System.out.println(sql);
				e.printStackTrace();
			}
			return this;
		}
	}
	public class Create extends SQLiteQuery implements Table.ICreateQuery{
		private boolean firstValue = true;
		public Create(Connection connection, String sql) {
			super(connection, "CREATE TABLE IF NOT EXISTS " + sql);
		}
		@Override
		public ICreateQuery add(List<Column> columns) {
			if(columns == null || columns.isEmpty())return this;
			if(firstValue) {
				sql += " (";
			}else
				sql = sql.substring(0, sql.length() - 1);
			for(Column column : columns) {
				if(firstValue)
					firstValue = false;
				else
					sql += ", ";
				sql += SQLiteTable.toString(column);
			}
			sql += ")";
			return this;
		}
	    public Create execute() {
	    	if(firstValue)	return this;
	    	try {
	    		prest = connection.createStatement();
	    		prest.execute(sql);
	    		close();
	        } catch (SQLException e) {
	    		System.out.println(sql);
	            e.printStackTrace();
	        }
			return this;
	    }
	}
	public class Insert extends SQLiteQuery implements Table.IInsertQuery {
		private boolean firstValue = true;
		private final List<Object> values;
		private String value = " VALUES ( ";
		public Insert(Connection connection, String table) {
			super(connection, "INSERT INTO " + table);
			values = new ArrayList<Object>();
		}
		public Insert insert(Column column, Object value) {
			values.add(value);
			if(firstValue){
				sql += " ( `" + column.getName() +"`)";
				this.value += "?)";
				firstValue = false;
			}else{
				sql = sql.substring(0, sql.length() - 1) + ", `" + column.getName() +"`)";
				this.value = this.value.substring(0, this.value.length() - 1) + ",?)";
			}
			return this;
		}
		public Insert execute() {
			if(firstValue) return this;
			try {
				prest = connection.prepareStatement(sql + value);
				int i = 1;
				for (Object object : values) {
					((PreparedStatement) prest).setObject(i, object);
					i++;
				}
				((PreparedStatement)prest).executeUpdate();
				close();
			} catch (SQLException e) {
				System.out.println(prest.toString());
				e.printStackTrace();
	        }
			return this;
	    }
	}
	public class Update extends SQLiteQuery implements Table.IUpdateQuery{
		private boolean comma,	and;
		private final List<Object> values = new ArrayList<Object>();
		public Update(Connection connection, String table) {
			super(connection, "UPDATE " + table + " SET");
			comma = false;
			and = false;
		}
		public Update set(Column column, Object value) {
			if (comma) {
				sql += ",";
			}else {
				comma = true;
			}
			values.add(value);
			sql += " `" + column.getName() + "` =?";
			return this;
		}
		public Update add(Column column, double value) {
			if (comma) {
				sql += ",";
			}else {
				comma = true;
			}
			sql += " `" + column.getName() + "` = " + " `" + column.getName() + "` + "+ value;
			return this;
		}
		public Update where(Column column, Object value) {
			if (and) {
				sql += " AND";
			} else {
				sql += " WHERE";
				and = true;
			}
			sql += " `" + column.getName() + "` =";
			values.add(value);
			sql += "?";
			return this;
		}
		public Update execute() {
	    	if(!comma) return this;
	    	try {
	    		prest = connection.prepareStatement(sql);
	    		int i = 1;
	    		for (Object object : values) {
	    			((PreparedStatement) prest).setObject(i, object);
	    			i++;
	    		}
	    		((PreparedStatement) prest).executeUpdate();
	    		close();
	        } catch (SQLException e) {
	    		System.out.println(prest.toString());
	            e.printStackTrace();
	        }
	    	return this;
	    }
	}
	public class Delete extends SQLiteQuery implements Table.IDeleteQuery{
		private boolean and;
		private final List<Object> values;
		public Delete(Connection connection, String table) {
			super(connection, "DELETE FROM " + table);
			and = false;
			values = new ArrayList<Object>();
		}
		public Delete where(Column column, Object value) {
			if (and) {
				sql += " AND";
			} else {
				sql += " WHERE";
				and = true;
			}
			sql += " " + column.getName() + "=?";
			values.add(value);
			return this;
		}
		public Delete execute() {
			if(!and)	return this;
			try {
				prest = connection.prepareStatement(sql);
				int i = 1;
				for (Object object : values) {
					((PreparedStatement) prest).setObject(i, object);
					i++;
				}
				((PreparedStatement) prest).executeUpdate();
				close();
			} catch (SQLException e) {
	    		System.out.println(prest.toString());
				e.printStackTrace();
			}
			return this;
		}
	}
	public static String typeToString(Type type) {
		switch (type) {
		case STRING_5:
			return "CHAR(5)";
		case STRING_8:
			return "CHAR(8)";
		case STRING_16:
			return "CHAR(16)";
		case STRING_32:
			return "CHAR(32)";
		case VARSTRING_8:
			return "VARCHAR(8)";
		case VARSTRING_16:
			return "VARCHAR(16)";
		case VARSTRING_32:
			return "VARCHAR(32)";
		case VARSTRING_64:
			return "VARCHAR(64)";
		case VARSTRING_128:
			return "VARCHAR(128)";
		case VARSTRING_255:
			return "VARCHAR(255)";
		case INTEGER:
			return "INTEGER";
		case DOUBLE:
			return "DECIMAL(12,3)";
		case TIME:
			return "DATETIME";
		case DATE:
			return "DATE";
		}
		return null;
	}
	public static String toString(Column column) {
		return " `" + column.getName() +"` " + SQLiteTable.typeToString(column.getType()) + " " +( column.notNull() ? "NOT NULL " : "") +
				( column.getDefaultValue() == null ? "" : ("DEFAULT " + parseDefaultValue(column.getType(), column.getDefaultValue().toString()) + " " )) +
				( column.unique() ? "UNIQUE " : "" ) +
				( column.primaryKey() ? "PRIMARY KEY " : "") +
				( column.increment() ? "AUTOINCREMENT " : "" );
	}
	public static String parseDefaultValue(Type type, String s) {
		if((type == Type.DATE || type == Type.TIME) && "current".equalsIgnoreCase(s)) return "CURRENT_TIMESTAMP";
		return s;
	}
	public void close() {
		if(connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connection = null;
		}
	}
}
