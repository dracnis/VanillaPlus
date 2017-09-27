package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Channel.Channel;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;

/**
 * This command allow you to talk in specified channel.
 * TYPE: CHANNEL_TALK
 * CHANNEL: id of channel // log if invalid.
 * SUCCESS: Message's path // default VOID, will send to sender's players as result if successfully executed. Use `sender` as [tag] for sender's placeholder and %message% for sender's message.
 *
 * Usage : <label> <message>
 * 
 * @author Soreth.
 */
public class CPChannelTalk extends CPSimple{
	private final Channel channel;
	public CPChannelTalk(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPChannelTalk(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		channel = VanillaPlusCore.getChannelManager().get(section.getString(Node.CHANNEL.get()), true);
		argumentRequired = 1;
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args) {
		String message = Utils.toString(args);
		if(receiver instanceof VPPlayer) {
			if(channel.sendMessage((VPPlayer) receiver, message, true))
				success.addReplacement("message", message);
			else
				return CommandResult.CANCELED;
		}else {
			channel.sendMessage(channel.getListeners(null, false), message);
			success.addReplacement("message", message);
		}
		return CommandResult.SUCCESS;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		if(args == null || args.isEmpty())return null;
		return VanillaPlusCore.getPlayerManager().getPlayersList(args.get(args.size()-1), true);
	}
	
}
