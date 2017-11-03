package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Channel.Channel;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you to edit channel settings.
 * TYPE: CHANNEL_STATE
 * CHANNEL: id of channel // log if invalid.
 * SWITCH_IN: true // default false, if true MUTE_IN and UNMUTE_IN are useless, channel will switch private's mute status.
 * SWITCH_OUT: true // default false, if true MUTE_OUT and UNMUTE_OUT are useless, channel will switch broadcast's mute status.
 * MUTE_IN: true // default false, if true UNMUTE_IN is useless, channel will mute private.
 * MUTE_OUT: true // default false, if true channel will mute broadcast.
 * UNMUTE_IN: true // default false, if true UNMUTE_OUT is useless, private will no longer be muted.
 * UNMUTE_OUT: true // default false, if true broadcast will no longer be muted.
 *
 * Usage : <label>
 * 
 * @author Soreth.
 */
public class CPChannelState extends CPSimple{
	private final Channel channel;
	private boolean switchIn, switchOut, muteIn, muteOut, unmuteIn, unmuteOut;
	public CPChannelState(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPChannelState(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		channel = VanillaPlusCore.getChannelManager().get(section.getString(Node.CHANNEL.get()), true);
		switchIn = section.getBoolean("SWITCH_IN", false);
		switchOut = section.getBoolean("SWITCH_OUT", false);
		if(!switchIn){
			muteIn = section.getBoolean("MUTE_IN", false);
			if(!muteIn)
				unmuteIn = section.getBoolean("UNMUTE_IN", false);
		}
		if(!switchOut){
			muteOut = section.getBoolean("MUTE_OUT", false);
			if(!muteOut)
				unmuteOut = section.getBoolean("UNMUTE_OUT", false);
		}
		if(!( switchIn || switchOut || muteIn || muteOut || unmuteIn || unmuteOut ))
			Error.INVALID.add();
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args) {
		byte success = 0;
		boolean contain = !( receiver instanceof VPPlayer) ? false : channel.contain((VPPlayer) receiver); 
		if(switchIn) {
			Message message = channel.setPrivate(!channel.getPrivate());
			if(message != null && !contain)
				message.sendTo(receiver);
			success ++;
		}
		if(switchOut) {
			Message message = channel.setBroadCast(!channel.getBroadCast());
			if(message != null && !contain)
				message.sendTo(receiver);
			success ++;
		}
		if(muteIn)
			if(channel.getPrivate()) {
				Message message = channel.setPrivate(false);
				if(message != null && !contain)
					message.sendTo(receiver);
				success ++;
			}
		if(muteOut)
			if(channel.getPrivate()) {
				Message message = channel.setBroadCast(false);
				if(message != null && !contain)
					message.sendTo(receiver);
				success ++;
			}
		if(unmuteIn)
			if(!channel.getPrivate()) {
				Message message = channel.setPrivate(true);
				if(message != null && !contain)
					message.sendTo(receiver);
				success ++;
			}
		if(unmuteOut)
			if(channel.getPrivate()) {
				Message message = channel.setBroadCast(true);
				if(message != null && !contain)
					message.sendTo(receiver);
				success ++;
			}
		return success == 0 ? CommandResult.CANCELED : CommandResult.SUCCESS;
		
	}
}
