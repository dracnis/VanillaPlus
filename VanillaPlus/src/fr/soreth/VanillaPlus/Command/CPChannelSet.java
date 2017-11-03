package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Channel.Channel;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you to edit your channel's settings.
 * TYPE: CHANNEL_SET
 * CHANNEL: id of channel // log if invalid.
 * SWITCH: true // default false, if true next nodes are useless, you will switch your channel status, join if not in else leave.
 * LEAVE: false // default false, if true next node is useless, you can no longer listen the channel.
 * JOIN: true // default false, if true next nodes are useless, you can listen the channel.
 * SET: false // default false, if true you will talk in the channel.
 *
 * Usage : <label> [player]
 * 
 * @author Soreth.
 */
public class CPChannelSet extends CPOther{
	private final Channel channel;
	private Message canceled, canceledOther, canceledTo;
	private boolean join, leave, set, switchState;
	public CPChannelSet(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPChannelSet(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		channel = VanillaPlusCore.getChannelManager().get(section.getString(Node.CHANNEL.get()), true);
		switchState = section.getBoolean(Node.SWITCH.get(), false);
		if(!switchState)
		leave = section.getBoolean(Node.LEAVE.get(), false);
		if(leave || switchState) {
			this.canceled				= manager.get(section.getString("CANCELED"));
			this.canceledOther			= manager.get(section.getString("CANCELED_OTHER"));
			this.canceledTo				= manager.get(section.getString("CANCELED_TO"));
			return;
		}
		join = section.getBoolean(Node.JOIN.get(), false);
		set = section.getBoolean(Node.SET.get(), false);
		if(!join && !set)
			Error.INVALID.add();
	}
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		if(receiver instanceof VPPlayer) {
			VPPlayer playerReceiver = (VPPlayer) receiver;
			if(switchState){
				channel.switchPlayer(playerReceiver);
				return CommandResult.SUCCESS;
			}
			if(set) {
				if(playerReceiver.getChannel() == channel) {
					if(other) {
						canceledOther.addSReplacement(PlaceH.SENDER.get(), sender).addSReplacement(PlaceH.RECEIVER.get(), receiver).sendTo(sender);
						canceledTo.addSReplacement(PlaceH.SENDER.get(), sender).addSReplacement(PlaceH.RECEIVER.get(), receiver).sendTo(receiver);
					}else {
						canceled.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
					}
					return CommandResult.CANCELED;
				}else {
					playerReceiver.setChannel(channel);
				}
			}
			boolean is = channel.contain(playerReceiver);
			if(join)
				if(is)
					return CommandResult.CANCELED;
				else
					channel.addPlayer(playerReceiver);
			else if(leave)
				if(is)
					if(playerReceiver.getChannel() != channel)
						channel.removePlayer(playerReceiver);
					else {
						if(other) {
							canceledOther.addSReplacement(PlaceH.SENDER.get(), sender).addSReplacement(PlaceH.RECEIVER.get(), receiver).sendTo(sender);
							canceledTo.addSReplacement(PlaceH.SENDER.get(), sender).addSReplacement(PlaceH.RECEIVER.get(), receiver).sendTo(receiver);
						}else {
							canceled.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
						}
						return CommandResult.CANCELED_OTHER;
					}
				else
					return CommandResult.CANCELED;
			return CommandResult.SUCCESS;
		}
		return CommandResult.FAIL;
	}
}
