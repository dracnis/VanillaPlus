package fr.soreth.VanillaPlus;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import fr.soreth.VanillaPlus.Player.VPSender;

public class VPCommandBlock implements VPSender{
	private BlockCommandSender sender;
	String nick;
	public VPCommandBlock(BlockCommandSender sender) {
		this.sender = sender;
	}
	@Override
	public Localizer getLanguage() {
		return VanillaPlusCore.getDefaultLang();
	}
	@Override
	public Location getLocation() {
		return sender.getBlock().getLocation();
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
		return sender.getName();
	}
	@Override
	public CommandSender getSender() {
		return sender;
	}
	@Override
	public void sendMessage(String message) {
		sender.sendMessage(message);
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
