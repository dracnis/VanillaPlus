package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you to set your time of day.
 * TYPE: TOD
 * VALUE: time wanted // if less than 0 and RELATIVE is false, will reset the time of day
 * RELATIVE: boolean // if true will add VALUE to player's time
 * ALREADY: Message's path // default VOID, will send to sender's players as result if TOD not changed.
 * ALREADY_OTHER: Message's path // default VOID, will send to sender's players as result if TOD not changed, 'receiver' for receiver placeholder.
 * ALREADY_TO: Message's path // default VOID, will send to receiver's players as result if TOD not changed, 'sender' for sender placeholder.
 *
 * Usage : <label> [player]
 * 
 * @author Soreth.
 */
public class CPTOD extends CPOther{
	private final int value;
	private final boolean relative;
	public CPTOD(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPTOD(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		value = section.getInt(Node.VALUE.get(),0);
		relative = section.getBoolean("RELATIVE");
	}
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		if(!(receiver instanceof VPPlayer))return CommandResult.FAIL;
		Player player = ((VPPlayer) receiver).getPlayer(); 
		if(value<0 && ! relative){
			if(!player.isPlayerTimeRelative())
				player.resetPlayerTime();
			else
				return CommandResult.CANCELED;
		}else{
			if(!relative && player.getPlayerTimeOffset() == value)
				return CommandResult.CANCELED;
			else
				player.setPlayerTime(value, relative);
		}
		return CommandResult.SUCCESS;
	}
}
