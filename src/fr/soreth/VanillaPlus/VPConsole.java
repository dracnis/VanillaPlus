package fr.soreth.VanillaPlus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import fr.soreth.VanillaPlus.Player.VPSender;

public class VPConsole implements VPSender{
	String nick;
	@Override
	public Localizer getLanguage() {
		return VanillaPlusCore.getDefaultLang();
	}
	@Override
	public Location getLocation() {
		return VanillaPlusCore.getPlayerManager().getServerSpawn();
	}
	@Override
	public String getName() {
		return nick == null ? getRealName() : nick;
	}
	@Override
	public String getNick() {
		return nick;
	}
	@Override
	public String getRealName() {
		return Bukkit.getConsoleSender().getName();
	}
	@Override
	public CommandSender getSender() {
		return Bukkit.getConsoleSender();
	}
	@Override
	public void sendMessage(String message) {
		Bukkit.getConsoleSender().sendMessage(message);
	}
	@Override
	public void setLanguage(Localizer language) {
		VanillaPlusCore.setDefaultLang(language);
	}
	@Override
	public void setNick(String nick) {
		this.nick = nick;
	}
	@Override
	public void setRealName(String name) {}
	@Override
	public void teleport(Location location) {
		VanillaPlusCore.getPlayerManager().setServerSpawn(location);
	}
}