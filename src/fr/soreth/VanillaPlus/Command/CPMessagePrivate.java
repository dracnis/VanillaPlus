package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.Channel.Channel;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.PlayerSettings;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.LiteEntry;
import fr.soreth.VanillaPlus.Utils.Utils;

/**
 * This command allow you to send private message.
 * TYPE: MESSAGE_PRIVATE
 * CHANNEL_SPY: Channel'id // can be null, log if invalid, if set will be used to log private messages.
 * MESSAGE_SPY: Message's path // default VOID, will send in CHANNEL_SPY if success. '%message%' for sender's message, 'sender' for sender placeholder,'receiver' for receiver placeholder.
 * BYPASS_CHANNEL_MUTE: requirement // default VOID, requirement required to bypass muted private message.
 * MESSAGE_FROM: Message's path // default VOID, sent to receiver, '%message%' for sender's message, 'sender' for sender placeholder,'receiver' for receiver placeholder.
 * MESSAGE_TO: Message's path // default VOID, sent to sender, '%message%' for sender's message, 'sender' for sender placeholder,'receiver' for receiver placeholder.
 * MESSAGE_TO_LOCKED: Message's path // default VOID, sent to sender if receiver has disabled private message and sender can't bypass, '%message%' for sender's message, 'sender' for sender placeholder,'receiver' for receiver placeholder.
 * MIN: int // default 0, minimum character required to send message.
 *
 * Usage : <label> [player] [message]
 * 
 * @author Soreth.
 */
public class CPMessagePrivate extends CommandPlus{
	private static CPMessagePrivate instance;
	private static HashMap<VPPlayer, HashMap<VPPlayer, LiteEntry<Integer, Integer>>>history = new HashMap<VPPlayer, HashMap<VPPlayer,LiteEntry<Integer,Integer>>>();
	private static HashMap<VPPlayer, LiteEntry<Integer, Integer>>consoleHistory = new HashMap<VPPlayer,LiteEntry<Integer,Integer>>();
	private final Message messageFrom, messageTo, messageToLockMP;
	private Message messageSpy;
	private Channel spy;
	private boolean bypassChannelMute;
	private int min = 0;
	public CPMessagePrivate(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPMessagePrivate(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		if(instance == null){
			instance = this;
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Entry<VPPlayer, HashMap<VPPlayer, LiteEntry<Integer, Integer>>>entry : history.entrySet()){
						if(entry.getValue() == null || entry.getValue().isEmpty()){
							history.remove(entry.getKey());
						}
						for(Entry<VPPlayer, LiteEntry<Integer, Integer>>pEntry : entry.getValue().entrySet()){
							if(pEntry.getValue() == null || pEntry.getValue().getValue() >=29){
								entry.getValue().remove(pEntry.getKey());
							}else{
								pEntry.getValue().setValue(pEntry.getValue().getValue()+1);
							}
						}
						if(entry.getValue().isEmpty()){
							history.remove(entry.getKey());
						}
					}
				}
			}.runTaskLater(VanillaPlus.getInstance(), 20);
		}
		if(section.contains("CHANNEL_SPY")){
			spy = VanillaPlusCore.getChannelManager().get(section.getString("CHANNEL_SPY"), true);
			messageSpy = manager.get(section.getString("MESSAGE_SPY"));
			if(spy == null || messageSpy == null){
				ErrorLogger.addError("SPY error");
			}
		}
		bypassChannelMute = section.getBoolean("BYPASS_CHANNEL_MUTE", false);
		messageFrom = manager.get(section.getString(Node.MESSAGE_FROM.get()));
		messageTo = manager.get(section.getString(Node.MESSAGE_TO.get()));
		messageToLockMP = manager.get(section.getString("MESSAGE_TO_LOCKED"));
		min = section.getInt("MIN", 0);
		if(min < 0)
			min = 0;
	}
	@Override
	public boolean onExecute(VPSender sender, String label, List<String> args) {
		if(args == null || args.isEmpty()) {
			sendUsage(sender, label);
			return false;
		}
		VPPlayer toSend = VanillaPlusCore.getPlayerManager().getPlayer(args.get(0));
		boolean isConsole = toSend == null && VanillaPlusCore.getVPConsole().getName().replace('§', '&').equalsIgnoreCase(args.get(0));
		args.remove(0);
		String msg = Utils.toString(args);
		if((isConsole && !(sender instanceof VPPlayer)) || (!isConsole && (toSend == null || msg.length() < min || !toSend.isOnline() || toSend == sender))) {
			sendUsage(sender, label);
			return false;
		}
		if(sender instanceof VPPlayer){
			VPPlayer player = (VPPlayer) sender;
			if((bypassChannelMute || player.getChannel().getPrivate() || player.getChannel().getBroadCast() ||
					VanillaPlusCore.getChannelManager().canBypassMute(player))){
				if(isConsole ? canAns(sender, true) : canAns(player, toSend, true)){
					messageFrom.addSReplacement(PlaceH.SENDER.get(), player)
						.addSReplacement(PlaceH.RECEIVER.get(), isConsole ? VanillaPlusCore.getVPConsole() : toSend)
						.addReplacement(PlaceH.MESSAGE.get(), Utils.toString(args))
						.sendTo( isConsole ? VanillaPlusCore.getVPConsole() : toSend);
					messageTo.addSReplacement(PlaceH.SENDER.get(), player)
						.addSReplacement(PlaceH.RECEIVER.get(),  isConsole ? VanillaPlusCore.getVPConsole() : toSend)
						.addReplacement(PlaceH.MESSAGE.get(), Utils.toString(args))
						.sendTo(sender);
					if(messageSpy != null){
						List<VPPlayer>list = spy.getListeners(player, true);
						list.remove(toSend);
						messageSpy.addSReplacement(PlaceH.SENDER.get(), player)
						.addSReplacement(PlaceH.RECEIVER.get(),  isConsole ? VanillaPlusCore.getVPConsole() : toSend)
						.addReplacement(PlaceH.MESSAGE.get(), Utils.toString(args)).send(list);
					}
				}else{
					if(messageToLockMP != null){
						messageToLockMP.addSReplacement(PlaceH.SENDER.get(), player)
						.addSReplacement(PlaceH.RECEIVER.get(),  isConsole ? VanillaPlusCore.getVPConsole() : toSend)
						.addReplacement(PlaceH.MESSAGE.get(), Utils.toString(args))
						.sendTo(sender);
					}
				}
			}else{
				player.getChannel().getMuted().sendTo(player);
			}
		return true;
		} else {
			LiteEntry<Integer, Integer>current = consoleHistory.get(toSend);
			if(current == null){
				current = new LiteEntry<Integer,Integer>(2, 0);
				consoleHistory.put(toSend, current);
			}
			current.setKey(2);
			messageFrom.addSReplacement(PlaceH.SENDER.get(), sender)
			.addSReplacement(PlaceH.RECEIVER.get(), toSend)
			.addReplacement(PlaceH.MESSAGE.get(), Utils.toString(args))
			.sendTo(toSend);
			messageTo.addSReplacement(PlaceH.SENDER.get(), sender)
			.addSReplacement(PlaceH.RECEIVER.get(), toSend)
			.addReplacement(PlaceH.MESSAGE.get(), Utils.toString(args))
			.sendTo(sender);
			if(messageSpy != null){
				List<VPPlayer>list = spy.getListeners(null, true);
				list.remove(toSend);
				messageSpy.addSReplacement(PlaceH.SENDER.get(), sender)
				.addSReplacement(PlaceH.RECEIVER.get(), toSend)
				.addReplacement(PlaceH.MESSAGE.get(), Utils.toString(args)).send(list);
			}
			return true;
		}
	}
	private boolean canAns(VPPlayer sender, VPPlayer toSend, boolean count) {
		if(count){
			HashMap<VPPlayer, LiteEntry<Integer, Integer>>current = history.get(sender);
			if(current == null){
				current = new HashMap<VPPlayer, LiteEntry<Integer,Integer>>();
				history.put(sender, current);
			}
			LiteEntry<Integer, Integer>lite = current.get(toSend);
			if(lite == null){
				lite = new LiteEntry<Integer, Integer>(2, 0);
				current.put(toSend, lite);
			}
			lite.setKey(2);
		}
		HashMap<VPPlayer, LiteEntry<Integer, Integer>>current = history.get(toSend);
		if(current == null)return toSend.getSetting(PlayerSettings.MP) || VanillaPlusCore.getPlayerManager().getBypassPM().has(sender);
		LiteEntry<Integer, Integer>lite = current.get(sender);
		if(lite == null)return toSend.getSetting(PlayerSettings.MP) || VanillaPlusCore.getPlayerManager().getBypassPM().has(sender);
		if(count){
			if(lite.getKey()==1){
				current.remove(sender);
				if(current.isEmpty())
					history.remove(toSend);
			}else{
				lite.setKey(lite.getKey()-1);
			}
		}
		return true;
	}
	private boolean canAns(VPSender sender, VPPlayer receiver, boolean count) {
		if(sender instanceof VPPlayer)
		return canAns((VPPlayer)sender, receiver, count);
		else
			return true;
	}
	private boolean canAns(VPSender sender, boolean count) {
		if(sender instanceof VPPlayer) {
			VPPlayer player = (VPPlayer) sender;
			LiteEntry<Integer, Integer>current = consoleHistory.get(player);
			if( current == null ) return VanillaPlusCore.getPlayerManager().getBypassPM().has(sender);
			if(count){
				if(current.getKey() == 1 ){
					consoleHistory.remove(sender);
				}else{
					current.setKey(current.getKey()-1);
				}
			}
			return true;
			
		}else
			return false;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		String alias = args.isEmpty() ? "" : args.get(args.size()-1);
		if(args.size() < 2) {
			List<String>result = new ArrayList<>();
			for (VPPlayer player : VanillaPlusCore.getPlayerManager().getPlayers(alias, true)) {
				if(player == sender)continue;
				if(canAns(sender, player, false))
					result.add(player.isNick() ? player.getName().replace('§', '&') : player.getName());
			}
			if(VanillaPlusCore.getVPConsole().getName().replace('§', '&').startsWith(alias) && !result.contains(VanillaPlusCore.getVPConsole().getName().replace('§', '&'))) {
				if(canAns(sender, false))
					result.add(VanillaPlusCore.getVPConsole().getName().replace('§', '&'));
			}
			return result;
		}else {
			return VanillaPlusCore.getPlayerManager().getPlayersList(alias, true);
		}
	}
	
}
