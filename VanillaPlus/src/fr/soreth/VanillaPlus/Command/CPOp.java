package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;

/**
 * This command perform a specified command as operator, almost useless. 
 * TYPE: OP
 * OP: sub command String or VanillaPlus command // log if invalid, will execute specified command as operator.
 *
 * Usage : <label> [arguments]
 * 
 * @author Soreth.
 */
public class CPOp extends CPOther{
	private CommandPlus command;
	private String cmd;
	public CPOp(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPOp(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		Object opCmd = section.get("OP");
		if(opCmd instanceof String){
			cmd = (String) opCmd;
		}else if(opCmd instanceof ConfigurationSection){
			command = VanillaPlusCore.getCommandManager().create(((ConfigurationSection) opCmd).getString(Node.TYPE.get(), Node.NODE.get()), opCmd);
		}else
			Error.INVALID.add();
	}
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		CommandResult result = CommandResult.SUCCESS;
		if(sender instanceof VPPlayer) {
			Player player = ((VPPlayer) sender).getPlayer();
			boolean is = player.isOp();
			player.setOp(true);
			if(command != null)
				command.onExecute(receiver, label, args);
			else if(cmd != null)
				player.performCommand(cmd.replaceAll("%sender_name%", receiver.getName()) + Utils.toString(args));
			else
				result = CommandResult.FAIL;
			player.setOp(is);
		}else {
			if(command != null)
				command.onExecute(receiver, label, args);
			else if(cmd != null)
				Bukkit.dispatchCommand(sender.getSender(), cmd.replaceAll("%sender_name%", receiver.getName()));
			else
				result = CommandResult.FAIL;
		}
		return result;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		if(command == null)return new ArrayList<String>();
		if(sender instanceof VPPlayer) {
			Player player = ((VPPlayer) sender).getPlayer();
			boolean is = player.isOp();
			player.setOp(true);
			List<String>result = command.onTab(sender, label, args);
			player.setOp(is);
			return result;
		}else
			return command.onTab(sender, label, args);
	}
	
}
