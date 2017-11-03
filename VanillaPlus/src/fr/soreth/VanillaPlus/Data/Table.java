package fr.soreth.VanillaPlus.Data;

import java.util.ArrayList;
import java.util.List;

public abstract class Table {
    protected final IConnection connection;
    final String table;
    protected final List<Column>columns = new ArrayList<Column>();
    protected Table(IConnection connection, String table) {
    	this.connection = connection;
    	this.table = table;
		select();
		alter();
		create();
		update();
		insert();
		delete();
    }
    public String getTableName() {
        return table;
    }
    public Table addColumn(Column column){
    	if(!columns.contains(column))
    		columns.add(column);
    	return this;
    }
    public Column getColumn(List<Column> columns, String name) {
    	if(columns == null)columns = this.columns;
    	for(Column c : columns)
    		if(c.getName().equals(name))return c;
    	return null;
    }
	public abstract Table validate();
    public abstract ISelectQuery select();
    public abstract IInsertQuery insert();
    public abstract IDeleteQuery delete();
    public abstract IUpdateQuery update();
    protected abstract ICreateQuery create();
    protected abstract IAlterQuery alter();

	public interface ISelectQuery{
		public ISelectQuery where(Column id, Object value);
		public IResultQuery execute();
		public void close();
	}
	public interface IInsertQuery{
		public IInsertQuery insert(Column id, Object value);
		public IInsertQuery execute();
		public void close();
	}
	public interface IDeleteQuery{
		public IDeleteQuery where(Column id, Object value);
		public IDeleteQuery execute();
		public void close();
	}
	public interface ICreateQuery{
		public ICreateQuery add(List<Column> columns);
		public ICreateQuery execute();
		public void close();
	}
	public interface IUpdateQuery{
		public IUpdateQuery set(Column column, Object value);
		public IUpdateQuery add(Column column, double d);
		public IUpdateQuery where(Column id, Object value);
		public IUpdateQuery execute();
		public void close();
	}
	public interface IAlterQuery{
		public IAlterQuery add(Column column);
		public IAlterQuery remove(Column column);
		public IAlterQuery modify(Column column);
		public IAlterQuery execute();
		public void close();
	}
}
