package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you to run multiple command from one.
 * TYPE: MULTI
 * NODE: List of sub commands
 *
 * Usage : <label> [arguments]
 * 
 * @author Soreth.
 */
public class CPMulti extends CommandPlus{
	private List<CommandPlus>commands;
	public CPMulti(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPMulti(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
    	commands = new ArrayList<CommandPlus>();
		ErrorLogger.addPrefix("SUB_LIST");
		ConfigurationSection node = section.getConfigurationSection("SUB_LIST");
		if(node == null)
			Error.INVALID.add();
		else
    	for(String key : node.getKeys(false)){
    		ErrorLogger.addPrefix(key);
    		ConfigurationSection sub = node.getConfigurationSection(key);
    		CommandPlus command = VanillaPlusCore.getCommandManager().create(sub.getString(Node.TYPE.get(), Node.NODE.get()), key, sub);
    		if(command != null)
    			commands.add(command);
    		ErrorLogger.removePrefix();
    	}
		ErrorLogger.removePrefix();
	}
	@Override
	public boolean onExecute(VPSender sender, String label, List<String> args) {
		for(CommandPlus command : commands)
			command.onExecute(sender, label, args);
		return true;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		return null;
	}

}
