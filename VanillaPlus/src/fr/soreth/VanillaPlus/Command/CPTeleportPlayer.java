package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you some teleport action.
 * PLAYER: boolean // default false, if true 
 * TYPE: TELEPORT_PLAYER
 * SUCCESS_OTHER: Message's path // default VOID, will send to sender's players as result if successfully executed to other player,
 *  'receiver' for receiver placeholder, 'destination' for player destination placeholder.
 * SUCCESS_TO: Message's path // default VOID, will send to receiver's players as result if successfully executed to other player,
 * 'sender' for sender placeholder, 'destination' for player destination placeholder.
 *
 * Usage : <label> [player] <player To>
 * 
 * @author Soreth.
 */
public class CPTeleportPlayer extends CPOther{
	private boolean random, online;
	public CPTeleportPlayer(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPTeleportPlayer(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		online			= section.getBoolean("ONLINE", false);
		random			= section.getBoolean(Node.RANDOM.get(), false);
		if(!random)
			argumentRequired ++;
		otherAtEnd = false;
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		if(receiver instanceof VPPlayer) {
			VPPlayer player = (VPPlayer) receiver;
			VPPlayer destination;
			if(random) {
				List<VPPlayer>list = VanillaPlusCore.getPlayerManager().getPlayers("", online);
				list.remove(player);
				destination = list.get(VanillaPlusCore.getRandom().nextInt(list.size()));
			}else
				destination = VanillaPlusCore.getPlayerManager().getPlayer(args.get(0));
			if(!player.isOnline() && online) {
				return CommandResult.FAIL;
			}
			if(player.equals(destination))
				return CommandResult.FAIL;
			player.teleport(destination.getLocation());
			if(other) {
				successOther.addSReplacement("destination", destination);
				successTo.addSReplacement("destination", destination);
			}else
				success.addSReplacement("destination", destination);
			return CommandResult.SUCCESS;
			
		}
		return CommandResult.FAIL;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		if ( args == null ) args = new ArrayList<>();
		List<String>result = new ArrayList<>();
		if ( args.size() < 2 ) {
			if(!random || otherRequirement.has(sender)) {
				result.addAll(VanillaPlusCore.getPlayerManager().getPlayersList(args.isEmpty() ? "" : args.get(0), online));
				result.remove(sender.getName());
			}
		}else if( args.size() == 2 && !random && otherRequirement.has(sender)) {
			result.addAll(VanillaPlusCore.getPlayerManager().getPlayersList(args.isEmpty() ? "" : args.get(1), online));
			result.remove(args.get(0));
		}
		return result;
	}
}
