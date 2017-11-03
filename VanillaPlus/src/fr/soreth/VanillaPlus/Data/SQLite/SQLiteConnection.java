package fr.soreth.VanillaPlus.Data.SQLite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Data.IConnection;
import fr.soreth.VanillaPlus.Data.Table;
import fr.soreth.VanillaPlus.Utils.ClassLoaderApi;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class SQLiteConnection implements IConnection{
    private final String database;
    private HashMap<String, SQLiteTable>table = new HashMap<>();
    private static final String name = "sqlite-jdbc", version = "3.20.0";
    private static final String MAVEN_FORMAT = "https://repo1.maven.org/maven2/org/xerial/%s/%s/%s-%s.jar";
    private static final String url = String.format(MAVEN_FORMAT, name, version, name, version);
	static{
		if(ReflectionUtils.getClass("org.sqlite.JDBC") == null) {
			ClassLoaderApi.loadJar(VanillaPlus.getInstance(), ClassLoaderApi.downloadJar(VanillaPlus.getInstance(), new File(VanillaPlus.getInstance().getDataFolder(), library), name+"_"+version , url));
			if(ReflectionUtils.getClass("org.sqlite.JDBC") == null) {
				ErrorLogger.addError("Can't load SQLite driver !");
			}
		}
	}
    public SQLiteConnection(ConfigurationSection storage, VanillaPlusExtension extension) {
    	String base = storage.getString("BASE");
    	this.database = extension.getInstance().getDataFolder().getAbsolutePath().toString() +
    			File.separatorChar + IConnection.storage + ( ( base == null || base.isEmpty() ) ? "" : File.separatorChar + base );
    	ConfigUtils.createRecursively(new File(database), true);
	}
    public Table getTable(String table) {
    	if(table == null || table.isEmpty()) {
    		ErrorLogger.addError("Table cannot be null !");
    		return null;
    	}
    	SQLiteTable result = this.table.get(table);
    	if(result == null) {
            String url = "jdbc:sqlite:" + this.database + File.separatorChar + table + ".db";
    		try {
				Connection coo = DriverManager.getConnection(url);
	        	Bukkit.getConsoleSender().sendMessage(prefix + "Successfully connected to " + url);
	        	result = new SQLiteTable(this, coo, table);
	        	this.table.put(table, result);
			} catch (SQLException e) {
	        	ErrorLogger.addError(prefix + "Can't connect to " + url);
			}
    	}
    	return result;
    }
	@Override
	public void close() {
		for(SQLiteTable table : this.table.values())
			table.close();
	}
}