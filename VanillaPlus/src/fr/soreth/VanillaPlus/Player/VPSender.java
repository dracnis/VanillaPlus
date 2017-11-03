package fr.soreth.VanillaPlus.Player;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import fr.soreth.VanillaPlus.Localizer;

public interface VPSender {
	public Localizer getLanguage();
	public Location getLocation();
	public String getName();
	public String getNick();
	public CommandSender getSender();
	public String getRealName();
	public void sendMessage(String message);
	public void setLanguage(Localizer local);
	public void setNick(String nick);
	public void setRealName(String name);
	public void teleport(Location location);
}
