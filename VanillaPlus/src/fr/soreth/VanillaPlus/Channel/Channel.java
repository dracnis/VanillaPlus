package fr.soreth.VanillaPlus.Channel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;

/**
 * This class represent a message channel.
 * TYPE: BASE // can remove this node.
 * ALLOW_IN: false // default false, if true player will can talk in channel, else if ALLOW_OUT is true player will talk in broadcast.
 * ALLOW_OUT: false // default false, if true player will can talk in broadcast with '!' or '*', if ALLOW_OUT and ALLOW_IN are false channel is muted.
 * ALONE: Message's path // default VOID, will send to players who talk alone. 'sender' for sender placeholder.
 * JOIN: Message's path // default VOID, will send to channel's players when a player join the channel. 'sender' for joiner placeholder.
 * LEAVE: Message's path // default VOID, will send to channel's players when a player leave the channel. 'sender' for leaver placeholder.
 * P_JOIN: Message's path // default VOID, will send to player who join the channel
 * P_LEAVE: Message's path // default VOID, will send to player who leave the channel
 * MUTED: Message's path // default VOID, will send to players who talk without talk power. 'sender' for sender placeholder.
 * MUTE_IN: Message's path // default VOID, will send to channel's players when private become muted.
 * MUTE_OUT: Message's path // default VOID, will send to channel's players when broadcast become muted.
 * TALK_IN: Message's path // default VOID, will send to channel's players. 'sender' for sender placeholder, '%message% for sender's message.
 * TALK_OUT: Message's path // default VOID, will send to all players. 'sender' for sender placeholder, '%message% for sender's message.
 * UNMUTE_IN: Message's path // default VOID, will send to channel's players when private is no longer muted.
 * UNMUTE_OUT: Message's path // default VOID, will send to channel's players when broadcast is no longer muted.
 *
 * @author Soreth.
 */
public class Channel{
	private final Message join, pJoin, leave, pLeave, talkIn, talkOut, alone, muted, muteIn, muteOut, unmuteIn, unmuteOut;
	private boolean allowOut, allowIn, tryBroadcast;
	private final byte minLength;
	private final List<VPPlayer>players = new ArrayList<VPPlayer>();

	public Channel(ConfigurationSection section, MessageManager manager) {
		allowIn			= section.getBoolean("ALLOW_IN",		false);
		allowOut		= section.getBoolean("ALLOW_OUT",		false);
		tryBroadcast	= section.getBoolean("TRY_OUT",			false);
		minLength		= (byte) section.getInt("MIN_CHAR", 	2);
		alone			= manager.get(section.getString("ALONE"));
		join			= manager.get(section.getString(Node.JOIN.get()));
		leave			= manager.get(section.getString(Node.LEAVE.get()));
		pJoin			= manager.get(section.getString("P_"+Node.JOIN.get()));
		pLeave			= manager.get(section.getString("P_"+Node.LEAVE.get()));
		muted			= manager.get(section.getString("MUTED"));
		muteIn			= manager.get(section.getString("MUTE_IN"));
		muteOut			= manager.get(section.getString("MUTE_OUT"));
		talkIn			= manager.get(section.getString("TALK_IN"));
		talkOut			= manager.get(section.getString("TALK_OUT"));
		unmuteIn		= manager.get(section.getString("UNMUTE_IN"));
		unmuteOut		= manager.get(section.getString("UNMUTE_OUT"));
		
	}
	/**
	 * Add player to this channel.
	 *
	 * @param player The player to add.
	 */
	public void addPlayer(VPPlayer player){
		if(!players.contains(player)){
			players.add(player);
			pJoin.sendTo(player);
			join.addSReplacement(PlaceH.SENDER.get(), player)
			.send(getListeners(player, true));
		}
	}
	/**
	 * Remove player from this channel.
	 *
	 * @param player The player to remove.
	 */
	public void removePlayer(VPPlayer player){
		if(players.contains(player)){
			leave.addSReplacement(PlaceH.SENDER.get(), player)
			.send(getListeners(player, true));
			players.remove(player);
			pLeave.sendTo(player);
		}
	}
	/**
	 * Send a message to the listeners.
	 *
	 * @param listeners List of VPPlayers.
	 * @param message The message to send.
	 * @return true if message was sent.
	 */
	public void sendMessage(List<VPPlayer> listeners, String message) {
		talkIn.addSReplacement(PlaceH.SENDER.get(), VanillaPlusCore.getVPConsole()).addReplacement(PlaceH.MESSAGE.get(), message).send(listeners);
	}
	/**
	 * Send a message from the sender in this channel.
	 *
	 * @param sender The sender.
	 * @param message The message sent by the sender.
	 * @param removeSender If the sender will be removed from the receiver's list.
	 * @return true if message was sent.
	 */
	public boolean sendMessage(VPPlayer sender, String message, boolean removeSender){
		if(message == null)
			return false;
		if(message.length() < minLength)
			return false;
		List<VPPlayer>listeners = getListeners(sender, removeSender);
		boolean broadcast = false;
		if( message.startsWith("*") || message.startsWith("!") ) {
			if(message.length() == minLength)
				return false;
			message = message.substring(1);
			if(message.startsWith(" "))
				message = message.substring(1);
			if(message.length() < minLength)
				return false;
			broadcast = true;
		}
		if( ( broadcast
			|| ( tryBroadcast
				&& ( ( !allowIn && allowOut && VanillaPlusCore.getPlayerManager().getOnlinePlayers().size() > 1)
					|| ( listeners.size() < getMin() && removeSender )
					|| ( listeners.size() == getMin() && !removeSender ) ) ) )
			&& ( allowOut || VanillaPlusCore.getChannelManager().canBypassMute(sender) ) ){
			talkOut.addSReplacement(PlaceH.SENDER.get(), sender)
			.addReplacement(PlaceH.MESSAGE.get(), message)
			.send();
			return true;
		}
		if(!allowIn && !VanillaPlusCore.getChannelManager().canBypassMute(sender)){
			muted.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
			return false;
		}
		if((listeners.size() < getMin() && removeSender)||
				(listeners.size() == getMin() && !removeSender)){
			alone.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(sender);
			return false;
		}
		talkIn.addSReplacement(PlaceH.SENDER.get(), sender).addReplacement(PlaceH.MESSAGE.get(), message).send(listeners);
		return true;
	}
	/**
	 * Remove player from this channel if in or add it if not.
	 *
	 * @param player The player to switch.
	 */
	public void switchPlayer(VPPlayer player){
		if(players.contains(player)) removePlayer(player);
		else addPlayer(player);
	}
	
	/**************************************************************
	 * 
	 * Getter and Setter
	 *
	 **************************************************************/
	
	/**
	 * Get the list of sender's listener.
	 *
	 * @param sender The sender.
	 * @param removeSender If the sender will be removed from the receiver's list.
	 * @return The list of receiver.
	 */
	public List<VPPlayer> getListeners(VPPlayer sender, boolean removeSender){
		List<VPPlayer>result = new ArrayList<VPPlayer>();
		if(sender == null) {
			result.addAll(players);
			return result;
		}
		for(VPPlayer player : players){
			if(player.isOnline())
				result.add(player);
		}
		if(removeSender)
			result.remove(sender);
		return result;
	}
	/**
	 * Get if can talk in broadcast on this channel.
	 *
	 * @return True If can talk in broadcast.
	 */
	public boolean getBroadCast(){
		return allowOut;
	}
	/**
	 * Get the minimum listener required to send message.
	 * If listener's size is lower than this value message will try broadcast if it's allowed,
	 * if player can't broadcast alone message will show up.
	 *
	 * @return The minimum required player.
	 */
	public int getMin(){
		return 1;
	}
	/**
	 * Get the muted message.
	 *
	 * @return the muted message.
	 */
	public Message getMuted(){
		return muted;
	}
	/**
	 * Get if can talk in private on this channel.
	 *
	 * @return True If can talk in private.
	 */
	public boolean getPrivate(){
		return allowIn;
	}
	public boolean contain(VPPlayer receiver) {
		return this.players.contains(receiver);
	}
	/**
	 * Set if can talk in broadcast on this channel.
	 *
	 * @param state If true, can talk in broadcast.
	 */
	public Message setBroadCast(boolean state){
		if(allowOut != state){
			allowOut = state;	
			if(state) {
				unmuteOut.send(players);
				return unmuteOut;
			}else {
				muteOut.send(players);
				return muteOut;
			}
		}
		return null;
	}
	/**
	 * Set if can talk in private on this channel.
	 *
	 * @param state If true, can talk in private.
	 * @return 
	 */
	public Message setPrivate(boolean state){
		if(allowIn != state){
			allowIn = state;	
			if(state) {
				unmuteIn.send(players);
				return unmuteIn;
			}else {
				muteIn.send(players);
				return muteIn;
			}
		}
		return null;
	}
}
