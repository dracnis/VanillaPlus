package fr.soreth.VanillaPlus.IReward;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Damage.Damage;
import fr.soreth.VanillaPlus.Damage.DamageManager;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RewardDamage implements IReward{
	private final Damage damage;
	private final MComponent format;
	public RewardDamage(ConfigurationSection section, MComponentManager manager) {
		damage = DamageManager.create(section.getConfigurationSection("DAMAGE"));
		format = manager.get(section.getString(Node.FORMAT.get(), "REWARD.DAMAGE"));
	}

	@Override
	public void give(VPPlayer player) {
		damage.damage(player);
	}
	@Override
	public void give(VPPlayer player, int time) {
		for(int i = 0 ; i < time ; i++)
			damage.damage(player);
	}
	@Override
	public String format(Localizer loc) {
		return format(loc, 1);
	}
	@Override
	public String format(Localizer loc, int amount) {
		return format.addReplacement("amount", String.valueOf(damage.getAmount()*amount))
				.addReplacement("amount_heart", String.valueOf(damage.getAmount()*amount*0.5))
				.getMessage(loc);
	}

}
