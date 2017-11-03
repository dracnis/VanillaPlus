package fr.soreth.VanillaPlus.Data;

import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class IConnectionManager extends Manager<String, IConnection>{
	public IConnectionManager(){
		super(String.class, IConnection.class);
		IConnectionLoader.load(this);
	}
	public void init(VanillaPlusExtension extension) {
		if(extension == null)return;
		ErrorLogger.addPrefix("Storage.yml");
		super.init(ConfigUtils.getYaml(extension.getInstance(), "Storage", false), extension);
		ErrorLogger.removePrefix();
	}
	public IConnection get(String connection) {
		if(connection == null)
			connection = "";
		IConnection result = super.get(connection, !connection.isEmpty());
		if(result == null && ! extensions.values().isEmpty())
			for(IConnection ic : extensions.values()) {
				result = ic;
				break;
			} 
		return result;
	}
	public void disable() {
		for(IConnection ic : extensions.values()) {
			ic.close();
		}
	}
}
