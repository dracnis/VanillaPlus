package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you change your name.
 * TYPE: NICK
 * RESET: Message's path // default VOID, will send to sender's players as result if remove nick ( write self nickname ).
 * RESET_OTHER: Message's path // default VOID, will send to sender's players as result if successfully executed, 'receiver' for receiver placeholder.
 * RESET_TO: Message's path // default VOID, will send to receiver's players as result if successfully executed, 'sender' for sender placeholder.
 * ALREADY: Message's path // default VOID, will send to sender's players as result if nickname is same as wanted.
 * ALREADY_OTHER: Message's path // default VOID, will send to sender's players as result if nickname is same as wanted, 'receiver' for receiver placeholder 'sender' for sender placeholder.
 * ALREADY_TO: Message's path // default VOID, will send to receiver's players as result if nickname is same as wanted, 'receiver' for receiver placeholder 'sender' for sender placeholder.
 * Usage : <label> [player] <new Name>
 *
 * @author Soreth.
 */

public class CPNick extends CPOther{ 
	private final Message reset, resetOther, resetTo;
	private final Requirement allowColor, allowCustomChar;
	private final String prefix;
	public CPNick(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPNick(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		reset				= manager.get(section.getString("RESET"));
		resetOther			= manager.get(section.getString("RESET_OTHER"));
		resetTo				= manager.get(section.getString("RESET_TO"));
		allowColor			= new Requirement(section.get("COLOR"), manager.getComponentManager());
		allowCustomChar		= new Requirement(section.get("CUSTOM_CHAR"), manager.getComponentManager());
		prefix				= section.getString("PREFIX", "");
		argumentRequired	= 1;
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		if(!(receiver instanceof VPPlayer))return CommandResult.FAIL;
		VPPlayer player = (VPPlayer) receiver;
		boolean color = allowColor.has(sender);
		String nick = color ? ChatColor.translateAlternateColorCodes('&', args.get(0)) : args.get(0);
		if(nick.equals("\"\"") || nick.equals(player.getRealName())){
			if(player.isNick()) {
				player.setNick(null);
				VanillaPlusCore.getPlayerManager().refreshName(player);
				if(other) {
					resetOther.addSReplacement(PlaceH.RECEIVER.get(), receiver).sendTo(sender);
					resetTo.addSReplacement(PlaceH.SENDER.get(), sender).sendTo(receiver);
				}else {
					reset.sendTo(player);
				}
				return CommandResult.CANCELED_OTHER;
			}else {
				return CommandResult.CANCELED;
			}
		}
		if(!allowCustomChar.has(sender) && !isValid(color ? ChatColor.stripColor(nick) : nick)){
			return CommandResult.FAIL;
		}
		nick = prefix + nick;
		if(nick.length() < 3 || nick.length() > 16){
			return CommandResult.FAIL;
		}
		if(nick.equals(player.getNick())){
			return CommandResult.CANCELED;
		}
		player.setNick(nick);
		VanillaPlusCore.getPlayerManager().refreshName(player);
		return CommandResult.SUCCESS;
	}
	private static boolean isValid(String nick){
		boolean valid = true;
		char[] a = nick.toCharArray();
		for (char c: a){
		    valid = ((c >= 'a') && (c <= 'z')) || 
		            ((c >= 'A') && (c <= 'Z')) || 
		            ((c >= '0') && (c <= '9') ||
		            		c == '_');

		    if (!valid)return false;
		}
		return valid;
	}
}
