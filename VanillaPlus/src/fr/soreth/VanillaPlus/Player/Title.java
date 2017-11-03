package fr.soreth.VanillaPlus.Player;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;

public class Title{
	private final MComponent name, description;
	private final int id;
	private final byte level;
	Title(int id,ConfigurationSection section, MComponentManager mComponentManager){
		this.id = id;
		name = mComponentManager.get(section.getString(Node.NAME_PATH.get()));
		description = mComponentManager.get(section.getString(Node.LORE_PATH.get()));
		int level = section.getInt(Node.LEVEL.get(), 1);
		if( level < 1 ){
			ErrorLogger.addError(Node.LEVEL.get() + " " + Error.INVALID.getMessage());
			this.level = 1;
		}else if( level > Byte.MAX_VALUE ){
			ErrorLogger.addError(Node.LEVEL.get() + " " + Error.INVALID.getMessage());
			this.level = Byte.MAX_VALUE;
		}else
			this.level = (byte) level;
	}
	public MComponent getName(){
		return name;
	}
	public MComponent getLore(){
		return description;
	}
	public int getID(){
		return id;
	}
	public short rank() {
		return level;
	}
}
