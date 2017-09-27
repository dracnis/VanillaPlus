package fr.soreth.VanillaPlus.MComponent;

import org.bukkit.Sound;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;

public class MCNote extends MComponent {
	private float volume, pitch = 1;
	private Sound sound;
	private String customSound;
	
	public MCNote(Localizer localizer, String message) {
		super(localizer, "");
		if(message == null)
			return;
		String[] temp = message.split(":", 3);
		if(temp.length==3){
			sound = Utils.matchEnum(Sound.values(), temp[0], null, true);
			if(sound == null){
				customSound = temp[0];
			}
			volume = Utils.parseFloat(temp[1], 1);
			pitch = Utils.parseFloat(temp[2], 1);
		}
	}
	@Override
	public void addLang(Localizer localizer, String message) {}
	@SuppressWarnings("deprecation")
	@Override
	protected void send(VPPlayer player, String message) {
		if(sound != null)
			player.getPlayer().playSound(player.getPlayer().getLocation(), sound, volume, pitch);
		else if (customSound != null)
			player.getPlayer().playSound(player.getPlayer().getLocation(), customSound, volume, pitch);
	}
	@Override
	public String getMessage(Localizer loc) {
		return "";
	}
	@Override
	public String getMessage() {
		return "";
	}
	@Override
	public boolean isStatic(){
		return true;
	}
	@Override
	public String getRawMessage(Localizer loc) {
		return "";
	}
	@Override
	public String getMessage(VPSender player){
		return "";
	}
	@Override
	public String getMessage(VPSender player, Localizer loc){
		return "";
	}
}