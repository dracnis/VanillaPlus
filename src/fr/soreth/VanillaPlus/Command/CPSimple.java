package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * SUCCESS: Message's path // default VOID, will send to sender's players as result if successfully executed.
 * ALREADY: Message's path // default VOID, will send to sender's players as result if successfully executed.
 *
 * @author Soreth.
 */

public abstract class CPSimple extends CommandPlus{
	protected Message success, already;
	protected int argumentRequired;
	public CPSimple(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPSimple(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		this.success		= manager.get(section.getString(Node.SUCCESS.get()));
		this.already		= manager.get(section.getString(Node.ALREADY.get()));
	}
	public boolean onExecute(VPSender sender, String label, List<String> args) {
		if(args == null)args = new ArrayList<>();
		if(args.size() < argumentRequired ){
			sendUsage(sender, label);
			return false;
		}
		switch(apply(sender, label, args)) {
		case SUCCESS:
			success.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
			if(sender instanceof VPPlayer)
				takeRequirement((VPPlayer) sender);
			return true;
		case CANCELED:
			already.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
			if(sender instanceof VPPlayer)
				takeRequirement(sender);
			return true;
		case FAIL:
			sendUsage(sender, label);
		default:
			return false;
			
		}
	}
	protected abstract CommandResult apply(VPSender receiver, String label, List<String> args);
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		return null;
	}
}
