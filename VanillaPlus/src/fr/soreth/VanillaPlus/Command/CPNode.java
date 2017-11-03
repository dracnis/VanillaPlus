package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;

/**
 * This command allow you to run multiple command from one.
 * TYPE: NODE
 * NODE: List of sub commands
 * DEFAULT: Default command // can be null, will run this command if arguments don't match with sub commands.
 *
 * Usage : <label> [arguments] or <label> <sub-command> [arguments] 
 * 
 * @author Soreth.
 */
public class CPNode extends CommandPlus{
	private List<CommandPlus>commands;
	private CommandPlus defaultCommand;
	public CPNode(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPNode(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
    	commands = new ArrayList<CommandPlus>();
    	ConfigurationSection node = section.getConfigurationSection(Node.NODE.getList());
		ErrorLogger.addPrefix(Node.NODE.getList());
    	if(node == null){
    		Error.INVALID.add();
    	}else{
    		for(String key : node.getKeys(false)){
    			ErrorLogger.addPrefix(key);
    			ConfigurationSection sub = node.getConfigurationSection(key);
    			CommandPlus command = VanillaPlusCore.getCommandManager().create(sub.getString(Node.TYPE.get(), Node.NODE.get()), sub, manager, key);
    			if(command != null)
    				commands.add(command);
    			ErrorLogger.removePrefix();
    		}
    	}
		ErrorLogger.removePrefix();
		if(section.contains(Node.DEFAULT.get())){
			ErrorLogger.addPrefix(Node.DEFAULT.get());
			defaultCommand = VanillaPlusCore.getCommandManager().create(section.getConfigurationSection(Node.DEFAULT.get())
					.getString(Node.TYPE.get(), Node.NODE.get()), section.getConfigurationSection(Node.DEFAULT.get()), manager, getName());
    		ErrorLogger.removePrefix();
		}
	}
	public int size(){
		return defaultCommand == null ? commands.size() : (commands.size()+1);
	}
	private void showHelp(VPSender sender, String label, int page){
		CPManager.showTopHelp(sender, label, page, this);
	}
	public List<CommandPlus> getSubs(){
		List<CommandPlus> result = new ArrayList<CommandPlus>();
		if(defaultCommand != null)
			result.add(defaultCommand);
		result.addAll(commands);
		return result;
	}
	@Override
	public boolean onExecute(VPSender sender, String label, List<String> args) {
		if(args == null)args = new ArrayList<>();
		if(args.size() >= 1 && args.get(0).equalsIgnoreCase(PlaceH.HELP.get())){
			showHelp(sender, label, ( args.size() >= 2 ) ? Utils.parseInt(args.get(1), 1, false) : 1);
			return true;
		}
		if(!args.isEmpty()){
			String name = args.get(0);
			String newlabel = label +" "+ name;
			args.remove(name);
			for(CommandPlus command : commands){
				if(command.is(name)){
					if(command.hasRequirement(sender))
						if(command.onExecute(sender, newlabel, args)) {
							command.takeRequirement(sender);
							return true;
						}else
							return false;
					
					return true;
				}
			}
			args.add(0, name);
		}
		if(defaultCommand!=null) {
			if(defaultCommand.hasRequirement(sender)) {
				if(defaultCommand.onExecute(sender, label, args)) {
					defaultCommand.takeRequirement(sender);
					return true;
				}else
					return false;
			}
		}else {
			showHelp(sender, label, 1);
		}
		return true;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		List<String>result = new ArrayList<String>();
		String name = "";
		String newlabel = label;
		if(!args.isEmpty()){
			name = args.get(0);
			name = name.toLowerCase();
			args.remove(name);
			newlabel += " "+ name;
		}
		for(CommandPlus command : commands){
			if(command.is(name)){
				if(command.hasRequirementSilent(sender))
					return command.onTab(sender, newlabel, args);
				else
					break;
			}
			if(command.getName().startsWith(name))
				if(command.hasRequirementSilent(sender)){
				result.add(command.getName());
				continue;
			}
			for(String alias : command.getAliases()){
				if(alias.startsWith(name))
					if(command.hasRequirementSilent(sender)){
						result.add(alias);
						break;
					}
			}
		}
		if(defaultCommand!=null)
			if(defaultCommand.hasRequirementSilent(sender)){
				List<String>subResult = defaultCommand.onTab(sender, label, args);
				if(subResult != null && !subResult.isEmpty())result.addAll(subResult);
			}
		return result;
	}

}
