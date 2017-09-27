package fr.soreth.VanillaPlus.Data;

import fr.soreth.VanillaPlus.ErrorLogger;

public class Column {
	public enum Type{
		STRING_5		{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 5);
			}
		},
		STRING_8		{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 8);
			}
		},
		STRING_16		{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 16);
			}
		},
		STRING_32		{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 32);
			}
		},
		VARSTRING_8		{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 8);
			}
		},
		VARSTRING_16	{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 16);
			}
		},
		VARSTRING_32	{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 32);
			}
		},
		VARSTRING_64	{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 64);
			}
		},
		VARSTRING_128	{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 128);
			}
		},
		VARSTRING_255	{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String && ((String)o).length() <= 255);
			}
		},
		INTEGER			{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof Integer);
			}
		},
		DOUBLE			{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof Double || o instanceof Integer);
			}
		},
		TIME			{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String
						|| o instanceof Integer);
			}
		},
		DATE			{
			@Override
			public boolean isValid(Object o) {
				return (o == null || o instanceof String
						|| o instanceof Integer);
			}
		};
		public abstract boolean isValid(Object o);

		};
		private String name;
		private int id;
		private Type type;
		private Object defaultValue;
		private boolean notNull, increment, unique, primaryKey;
		public Column(String name, Type type) {
			this.name = name;
			this.type = type;
		}
		public Column(String name, Type type, Object defaultValue, boolean notNull, boolean increment, boolean unique, boolean primaryKey) {
			if(name == null)
				ErrorLogger.addError("Name of table cannot be null !");
			this.name = name;
			this.type = type;
			this.notNull = notNull;
			this.defaultValue = defaultValue;
			if(increment)
				if(type == Type.INTEGER)
					this.increment = true;
				else
					ErrorLogger.addError(name + " cannot increment !");
			this.primaryKey = primaryKey;
			if(!primaryKey)
				this.unique = unique;
		}
		public String getName() {
			return this.name;
		}
		public int getId() {
			return this.id;
		}
		public Object getDefaultValue() {
			return this.defaultValue;
		}
		public boolean increment() {
			return this.increment;
		}
		public boolean unique() {
			return unique;
		}
		public boolean primaryKey() {
			return primaryKey;
		}
		public boolean notNull() {
			return this.notNull;
		}
		public void setId(int id) {
			if(id > 0)
			this.id = id;
		}
		public Type getType() {
			return this.type;
		}
		public boolean isValid(Object o) {
			if(o == null && notNull) return false;
			return type.isValid(o);
		}
}
