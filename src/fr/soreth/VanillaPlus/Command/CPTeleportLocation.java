package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

/**
 * This command allow you some teleport action. 
 * TYPE: TELEPORT_PLAYER
 * ONLINE: boolean // default false, if true will allow only online player.
 *
 * Usage : <label> [player]
 * 
 * @author Soreth.
 */
public class CPTeleportLocation extends CPOther{
	private Location loc;
	private boolean online;
	public CPTeleportLocation(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPTeleportLocation(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		online			= section.getBoolean("ONLINE", false);
		if(section.contains(Node.LOCATION.get())) {
			ConfigurationSection temp = section.getConfigurationSection(Node.LOCATION.get());
			ErrorLogger.addPrefix(Node.LOCATION.get());
			loc = ConfigUtils.loadLocation(temp);
			ErrorLogger.removePrefix();
		}else if(section.contains(Node.WORLD.get())){
			ErrorLogger.addPrefix(Node.WORLD.get());
			String world = section.getString(Node.WORLD.get());
			ErrorLogger.removePrefix();
			if(world != null) {
				World w = Bukkit.getWorld(world);
				if(w != null) {
					loc = w.getSpawnLocation();
				}else
					ErrorLogger.addError("World `" + world + "` wasn't found.");
			}else
				Error.INVALID.add();
		}
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		if(receiver instanceof VPPlayer) {
			VPPlayer player = (VPPlayer) receiver;
			if(!player.isOnline() && online) {
				return CommandResult.FAIL;
			}
			player.teleport(loc == null ? VanillaPlusCore.getPlayerManager().getServerSpawn() : loc);
			return CommandResult.SUCCESS;
			
		}
		return CommandResult.FAIL;
	}	
}
