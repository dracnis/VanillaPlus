package fr.soreth.VanillaPlus.Data;

import org.bukkit.ChatColor;

public interface IConnection {
	final static String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6Data&7-&6Api&8]&a");
	final static String library = "Library", storage = "Storage";
	public Table getTable(String table);
	public void close();
}
