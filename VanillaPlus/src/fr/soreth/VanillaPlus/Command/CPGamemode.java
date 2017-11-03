package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;


/**
 * This command allow you to change gamemode with permission.
 * TYPE: GAMEMODE
 * GAMEMODE: gamemode // log if invalid.
 * ALREADY: Message's path // default VOID, will send to sender's players as result if had already required gamemode.
 * ALREADY_OTHER: Message's path // default VOID, will send to sender's players as result if had already required gamemode, 'receiver' for receiver placeholder.
 * ALREADY_TO: Message's path // default VOID, will send to receiver's players as result if had already required gamemode, 'sender' for sender placeholder.
 * 
 * Usage : <label> [player]
 *
 * @author Soreth.
 */

public class CPGamemode extends CPOther{
	private final GameMode gm;
	public CPGamemode(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	@SuppressWarnings("deprecation")
	public CPGamemode(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		ErrorLogger.addPrefix("GAMEMODE");
		Object o = section.get("GAMEMODE");
		if(o instanceof Integer){
			gm = GameMode.getByValue((int) o);
			if(gm == null){
				Error.INVALID.add();
			}
		}else if(o instanceof String){
			gm = GameMode.valueOf((String) o);
			if(gm == null){
				Error.INVALID.add();
			}
		}else {
			gm = GameMode.SURVIVAL;
			Error.INVALID.add();
		}
		ErrorLogger.removePrefix();
	}
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		if(receiver instanceof VPPlayer) {
			if(((VPPlayer) receiver).getGameMode() == gm) {
				return CommandResult.CANCELED;
			}else {
				((VPPlayer) receiver).setGameMode(gm);
				return CommandResult.SUCCESS;
			}
		}else
			return CommandResult.FAIL;
	}	
}
