package fr.soreth.VanillaPlus.Data;

import fr.soreth.VanillaPlus.Data.SQLite.SQLiteConnection;

public class IConnectionLoader {
	private static boolean init;

	public static void load(IConnectionManager manager) {
		if(init)return;
		init = true;
		manager.register(SQLiteConnection.class, "SQLITE");
	}
	
}
