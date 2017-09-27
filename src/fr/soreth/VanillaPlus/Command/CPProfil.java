package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.PlayerSettings;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you edit player's settings.
 * TYPE: PROFIL
 * PLAYER_SETTING: PlayerSettings // The setting to edit
 * SWITCH: boolean // default false, if true will switch the setting
 * ENABLE: boolean // default false, if SWITCH is true this one is useless otherwise if set on true will set the setting to true else to false.
 * ALREADY: Message's path // default VOID, will send to sender's players as result if SWITCH is false and setting same as ENABLE.
 * ALREADY_OTHER: Message's path // default VOID, will send to sender's players as result if SWITCH is false and setting same as ENABLE, 'receiver' for receiver placeholder.
 * ALREADY_TO: Message's path // default VOID, will send to receiver's players as result if SWITCH is false and setting same as ENABLE, 'sender' for sender placeholder.
 * Usage : <label> [player]
 *
 * @author Soreth.
 */

public class CPProfil extends CPOther {
	private final PlayerSettings setting;
	private boolean enable, switchState;
	public CPProfil(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPProfil(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		setting = PlayerSettings.valueOf(section.getString("PLAYER_SETTING"));
		if(setting == null)
			ErrorLogger.addError("PLAYER_SETTING " + Error.INVALID.getMessage());
		switchState = section.getBoolean("SWITCH", false); 
		enable = section.getBoolean("ENABLE", false);
	}
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		if(!(receiver instanceof VPPlayer))return CommandResult.FAIL;
		VPPlayer player = (VPPlayer) receiver; 
		boolean current = player.getSetting(setting);
		if(switchState){
			player.setSetting(setting, !current);
			return CommandResult.SUCCESS;
		}else{
			if(current != enable){
				player.setSetting(setting, enable);
				return CommandResult.SUCCESS;
			}
			return CommandResult.CANCELED;
		}
	}
}
