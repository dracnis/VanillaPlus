package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * OTHER_REQUIREMENT: Requirement to use this command on other player.
 * SUCCESS_OTHER: Message's path // default VOID, will send to sender's players as result if successfully executed to other player, 'receiver' for receiver placeholder.
 * SUCCESS_TO: Message's path // default VOID, will send to receiver's players as result if successfully executed to other player, 'sender' for sender placeholder.
 * ALREADY_OTHER: Message's path // default VOID, will send to sender's players as result if successfully executed to other player, 'receiver' for receiver placeholder.
 * ALREADY_TO: Message's path // default VOID, will send to receiver's players as result if successfully executed to other player, 'sender' for sender placeholder.
 *
 * @author Soreth.
 */

public abstract class CPOther extends CPSimple{
	protected final Requirement otherRequirement;
	protected final Message alreadyOther, alreadyTo, successOther, successTo;
	protected boolean otherAtEnd, online = true;
	public CPOther(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPOther(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		this.otherRequirement		= new Requirement(section.get(Node.REQUIREMENT.getOther()), manager.getComponentManager());
		this.alreadyOther			= manager.get(section.getString(Node.ALREADY.getOther()));
		this.alreadyTo				= manager.get(section.getString(Node.ALREADY.get()+"_TO"));
		this.successOther			= manager.get(section.getString(Node.SUCCESS.getOther()));
		this.successTo				= manager.get(section.getString(Node.SUCCESS.get()+"_TO"));
	}
	@Override
	public boolean onExecute(VPSender sender, String label, List<String> args) {
		if( ( argumentRequired == 0 && args == null ) || args.size() < argumentRequired ){
			sendUsage(sender, label);
			return false;
		}
		if(args != null && args.size() >= argumentRequired + 1 && otherRequirement.has(sender)) {
			VPPlayer player = VanillaPlusCore.getPlayerManager().getPlayer(args.get(otherAtEnd ? args.size() -1 : 0));
			if( player != null && !sender.equals(player)){
				args.remove(otherAtEnd ? args.size() -1 : 0);
				switch(apply(player, label, args, true, sender)) {
				case SUCCESS:
					successOther.addSReplacement(PlaceH.SENDER.get(), sender).addSReplacement(PlaceH.RECEIVER.get(), player).sendTo(sender);
					successTo.addSReplacement(PlaceH.SENDER.get(), sender).addSReplacement(PlaceH.RECEIVER.get(), player).sendTo(player);
					takeRequirement(sender);
					return true;
				case CANCELED:
					alreadyOther.addSReplacement(PlaceH.SENDER.get(), sender).addSReplacement(PlaceH.RECEIVER.get(), player).sendTo(sender);
					alreadyTo.addSReplacement(PlaceH.SENDER.get(), sender).addSReplacement(PlaceH.RECEIVER.get(), player).sendTo(player);
					return true;
				case FAIL:
					sendUsage(sender, label);
				default:
					return false;
				}
			}
		}
		if(sender instanceof VPPlayer) {
			switch(apply(sender, label, args, false, sender)) {
			case SUCCESS:
				success.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
				takeRequirement(sender);
				return true;
			case CANCELED:
				already.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
				return true;
			case FAIL:
				sendUsage(sender, label);
			default:
				return false;	
			}
		}
		sendUsage(sender, label);
		return false;
	}
	protected CommandResult apply(VPSender receiver, String label, List<String> args) {
		return apply(receiver, label, args, false, receiver);
	}
	protected abstract CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender);
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		if ( args == null ) args = new ArrayList<>();
		List<String>result = new ArrayList<>();
		if (!otherAtEnd && args.size() < 2 && otherRequirement.has(sender) ) {
			result.addAll(VanillaPlusCore.getPlayerManager().getPlayersList(args.isEmpty() ? "" : args.get(0), online));
			result.remove(sender.getName());
		}
		return result;
	}
}
