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
import fr.soreth.VanillaPlus.Utils.Utils;

/**
 * This command allow you to send specific message.
 * TYPE: MESSAGE_SEND
 * MESSAGE: Message's path // default VOID, '%message%' for sender's message, 'sender' for sender placeholder.
 * ALL: boolean // default false, if true, message will be send as broadcast else will be send as sender's message.
 * PRIVATE: boolean // default false, if true, message will be send only to the sender, will not be send to receiver if ALLOW_OTHER is true.
 * ALLOW_OTHER: boolean // default false, if true, player with OTHER_REQUIREMENT will can send message to other players.
 * OTHER_REQUIREMENT: requirement // default VOID, requirement required to send to other player / view SUCCESS_OTHER.
 * SUCCESS_OTHER: Message's path // default VOID, will send to sender if success other player. '%message%' for sender's message, 'sender' for sender placeholder,'receiver' for receiver placeholder.
 *
 * Usage : <label> [player]
 * 
 * @author Soreth.
 */
public class CPMessageSend extends CommandPlus{
	private final Message message, successOther;
	private final Requirement other;
	private boolean all, priv;
	public CPMessageSend(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPMessageSend(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		message			= manager.get(section.getString(Node.MESSAGE.get()));
		all				= section.getBoolean("ALL", false);
		priv			= section.getBoolean("PRIVATE", false);
		other			= new Requirement(section.get(Node.OTHER_REQUIREMENT.get()), manager.getComponentManager());
		successOther	= manager.get(section.getString(Node.SUCCESS.getOther()));
	}
	@Override
	public boolean onExecute(VPSender sender, String label, List<String> args) {
		if(args != null && !args.isEmpty()){
			if(other.has(sender)){
				VPPlayer toSend = VanillaPlusCore.getPlayerManager().getPlayer(args.get(0));
				if(toSend == null || ( toSend == sender && !priv ) || !toSend.isOnline()){
					sendUsage(sender, label);
				}else{
					if(!priv)
					message.addSReplacement(PlaceH.SENDER.get(), sender)
					.sendTo(toSend);
					successOther.addSReplacement(PlaceH.RECEIVER.get(), toSend)
					.sendTo(sender);
					takeRequirement(sender);
				}
				return true;
			}
		}
		message.addReplacement(PlaceH.MESSAGE.get(), Utils.toString(args));
		if(all)
			message.addSReplacement(PlaceH.SENDER.get(), sender).send();
		else if(priv)
			message.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
		else if(sender instanceof VPPlayer)
			message.addSReplacement(PlaceH.SENDER.get(), sender).send((VPPlayer) sender);
		else
			message.addSReplacement(PlaceH.SENDER.get(), sender).send();
		takeRequirement(sender);
		return true;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		if(args == null) args = new ArrayList<String>();
		List<String>result = VanillaPlusCore.getPlayerManager().getPlayersList(args.isEmpty() ? "" : args.get(args.size()-1), true);
		if(!priv && args.size() <= 1 && other.has(sender))result.remove(sender.getName());
		return result;
	}
	
}
