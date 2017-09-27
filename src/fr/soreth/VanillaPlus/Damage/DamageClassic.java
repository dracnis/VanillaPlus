package fr.soreth.VanillaPlus.Damage;

import org.bukkit.configuration.ConfigurationSection;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class DamageClassic extends Damage {

	private String type;
	public DamageClassic(ConfigurationSection section){
		super(section);
		if(section == null)return;
		type = section.getString("DAMAGE_TYPE");
	}

	@Override
	public boolean damage(VPPlayer player) {
		if(player.getPlayer().getHealth()<=amount){
			player.getVersus().setCustom(type);
			player.kill();
			return true;
		}else
			player.getPlayer().damage(amount);
		return false;
	}

}
